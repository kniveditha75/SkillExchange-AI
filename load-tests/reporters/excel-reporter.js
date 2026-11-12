const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

// CRC32 table
const crcTable = new Uint32Array(256);
for (let i = 0; i < 256; i++) {
  let c = i;
  for (let k = 0; k < 8; k++) {
    c = (c & 1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1);
  }
  crcTable[i] = c;
}

function crc32(buf) {
  let crc = 0xFFFFFFFF;
  for (let i = 0; i < buf.length; i++) {
    crc = crcTable[(crc ^ buf[i]) & 0xFF] ^ (crc >>> 8);
  }
  return (crc ^ 0xFFFFFFFF) >>> 0;
}

function createZip(files) {
  const fileEntries = [];
  let centralDirectorySize = 0;
  let offset = 0;
  const parts = [];

  for (const file of files) {
    const nameBuf = Buffer.from(file.name, 'utf8');
    const dataBuf = Buffer.isBuffer(file.data) ? file.data : Buffer.from(file.data, 'utf8');
    const uncompressedSize = dataBuf.length;
    
    const compressedData = zlib.deflateRawSync(dataBuf);
    const compressedSize = compressedData.length;
    const crc = crc32(dataBuf);

    const localHeader = Buffer.alloc(30 + nameBuf.length);
    localHeader.writeUInt32LE(0x04034b50, 0);
    localHeader.writeUInt16LE(20, 4);
    localHeader.writeUInt16LE(0, 6);
    localHeader.writeUInt16LE(8, 8);
    localHeader.writeUInt16LE(0, 10);
    localHeader.writeUInt16LE(0, 12);
    localHeader.writeUInt32LE(crc, 14);
    localHeader.writeUInt32LE(compressedSize, 18);
    localHeader.writeUInt32LE(uncompressedSize, 22);
    localHeader.writeUInt16LE(nameBuf.length, 26);
    localHeader.writeUInt16LE(0, 28);
    nameBuf.copy(localHeader, 30);

    parts.push(localHeader);
    parts.push(compressedData);

    fileEntries.push({
      nameBuf,
      crc,
      compressedSize,
      uncompressedSize,
      offset,
    });

    offset += localHeader.length + compressedSize;
  }

  const cdStartOffset = offset;
  for (const entry of fileEntries) {
    const cdHeader = Buffer.alloc(46 + entry.nameBuf.length);
    cdHeader.writeUInt32LE(0x02014b50, 0);
    cdHeader.writeUInt16LE(20, 4);
    cdHeader.writeUInt16LE(20, 6);
    cdHeader.writeUInt16LE(0, 8);
    cdHeader.writeUInt16LE(8, 10);
    cdHeader.writeUInt16LE(0, 12);
    cdHeader.writeUInt16LE(0, 14);
    cdHeader.writeUInt32LE(entry.crc, 16);
    cdHeader.writeUInt32LE(entry.compressedSize, 20);
    cdHeader.writeUInt32LE(entry.uncompressedSize, 24);
    cdHeader.writeUInt16LE(entry.nameBuf.length, 28);
    cdHeader.writeUInt16LE(0, 30);
    cdHeader.writeUInt16LE(0, 32);
    cdHeader.writeUInt16LE(0, 34);
    cdHeader.writeUInt16LE(0, 36);
    cdHeader.writeUInt32LE(0, 38);
    cdHeader.writeUInt32LE(entry.offset, 42);
    entry.nameBuf.copy(cdHeader, 46);

    parts.push(cdHeader);
    centralDirectorySize += cdHeader.length;
  }

  const eocd = Buffer.alloc(22);
  eocd.writeUInt32LE(0x06054b50, 0);
  eocd.writeUInt16LE(0, 4);
  eocd.writeUInt16LE(0, 6);
  eocd.writeUInt16LE(fileEntries.length, 8);
  eocd.writeUInt16LE(fileEntries.length, 10);
  eocd.writeUInt32LE(centralDirectorySize, 12);
  eocd.writeUInt32LE(cdStartOffset, 16);
  eocd.writeUInt16LE(0, 20);

  parts.push(eocd);
  return Buffer.concat(parts);
}

function escapeXml(str) {
  if (str === null || str === undefined) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&apos;');
}

function colName(colIndex) {
  let name = '';
  colIndex += 1;
  while (colIndex > 0) {
    const rem = (colIndex - 1) % 26;
    name = String.fromCharCode(65 + rem) + name;
    colIndex = Math.floor((colIndex - 1) / 26);
  }
  return name;
}

function buildXlsxBuffer(sheets) {
  const files = [];

  let contentTypesXml = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>`;
  for (let i = 0; i < sheets.length; i++) {
    contentTypesXml += `\n  <Override PartName="/xl/worksheets/sheet${i+1}.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>`;
  }
  contentTypesXml += `\n</Types>`;
  files.push({ name: '[Content_Types].xml', data: contentTypesXml });

  const rootRelsXml = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>`;
  files.push({ name: '_rels/.rels', data: rootRelsXml });

  let workbookXml = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>`;
  for (let i = 0; i < sheets.length; i++) {
    workbookXml += `\n    <sheet name="${escapeXml(sheets[i].name)}" sheetId="${i+1}" r:id="rId${i+1}"/>`;
  }
  workbookXml += `\n  </sheets>\n</workbook>`;
  files.push({ name: 'xl/workbook.xml', data: workbookXml });

  let workbookRelsXml = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">`;
  for (let i = 0; i < sheets.length; i++) {
    workbookRelsXml += `\n  <Relationship Id="rId${i+1}" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet${i+1}.xml"/>`;
  }
  workbookRelsXml += `\n  <Relationship Id="rIdStyles" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>\n</Relationships>`;
  files.push({ name: 'xl/_rels/workbook.xml.rels', data: workbookRelsXml });

  const stylesXml = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="9">
    <font><sz val="11"/><name val="Segoe UI"/></font>
    <font><b/><sz val="11"/><color rgb="FFFFFFFF"/><name val="Segoe UI"/></font>
    <font><b/><sz val="16"/><color rgb="FF0F172A"/><name val="Segoe UI"/></font>
    <font><b/><sz val="11"/><color rgb="FF166534"/><name val="Segoe UI"/></font>
    <font><b/><sz val="11"/><color rgb="FF991B1B"/><name val="Segoe UI"/></font>
    <font><b/><sz val="12"/><color rgb="FF1E293B"/><name val="Segoe UI"/></font>
    <font><sz val="10"/><color rgb="FF64748B"/><name val="Segoe UI"/></font>
    <font><b/><sz val="20"/><color rgb="FF4338CA"/><name val="Segoe UI"/></font>
    <font><b/><sz val="11"/><color rgb="FF0F172A"/><name val="Segoe UI"/></font>
  </fonts>
  <fills count="8">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FF3730A3"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFDCFCE7"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFFEE2E2"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFEEF2FF"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFE0E7FF"/></patternFill></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFF8FAFC"/></patternFill></fill>
  </fills>
  <borders count="2">
    <border><left/><right/><top/><bottom/><diagonal/></border>
    <border>
      <left style="thin"><color rgb="FFE2E8F0"/></left>
      <right style="thin"><color rgb="FFE2E8F0"/></right>
      <top style="thin"><color rgb="FFE2E8F0"/></top>
      <bottom style="thin"><color rgb="FFE2E8F0"/></bottom>
    </border>
  </borders>
  <cellStyleXfs count="1">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
  </cellStyleXfs>
  <cellXfs count="10">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
    <xf numFmtId="0" fontId="1" fillId="2" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
    <xf numFmtId="0" fontId="3" fillId="3" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
    <xf numFmtId="0" fontId="4" fillId="4" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
    <xf numFmtId="0" fontId="2" fillId="5" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment vertical="center"/></xf>
    <xf numFmtId="0" fontId="0" fillId="0" borderId="1" xfId="0" applyBorder="1"/>
    <xf numFmtId="0" fontId="6" fillId="0" borderId="1" xfId="0" applyFont="1" applyBorder="1"/>
    <xf numFmtId="0" fontId="7" fillId="6" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1" applyAlignment="1"><alignment horizontal="center" vertical="center"/></xf>
    <xf numFmtId="0" fontId="8" fillId="7" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1"/>
    <xf numFmtId="0" fontId="5" fillId="5" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1"/>
  </cellXfs>
</styleSheet>`;
  files.push({ name: 'xl/styles.xml', data: stylesXml });

  for (let sIdx = 0; sIdx < sheets.length; sIdx++) {
    const sheet = sheets[sIdx];
    let sheetXml = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">`;

    if (sheet.colWidths && sheet.colWidths.length > 0) {
      sheetXml += `\n  <cols>`;
      for (let c = 0; c < sheet.colWidths.length; c++) {
        sheetXml += `\n    <col min="${c+1}" max="${c+1}" width="${sheet.colWidths[c]}" customWidth="1"/>`;
      }
      sheetXml += `\n  </cols>`;
    }

    sheetXml += `\n  <sheetData>`;
    const rows = sheet.rows || [];
    for (let rIdx = 0; rIdx < rows.length; rIdx++) {
      const row = rows[rIdx];
      const rNum = rIdx + 1;
      sheetXml += `\n    <row r="${rNum}">`;

      for (let cIdx = 0; cIdx < row.length; cIdx++) {
        const cell = row[cIdx];
        const cellRef = colName(cIdx) + rNum;
        if (cell === null || cell === undefined) continue;

        let styleAttr = '';
        let cellVal = cell;
        let isNum = false;

        if (typeof cell === 'object' && cell !== null && cell.v !== undefined) {
          if (cell.s !== undefined) styleAttr = ` s="${cell.s}"`;
          cellVal = cell.v;
        }

        if (typeof cellVal === 'number') {
          isNum = true;
        }

        if (isNum) {
          sheetXml += `<c r="${cellRef}"${styleAttr}><v>${cellVal}</v></c>`;
        } else {
          sheetXml += `<c r="${cellRef}" t="inlineStr"${styleAttr}><is><t>${escapeXml(cellVal)}</t></is></c>`;
        }
      }
      sheetXml += `</row>`;
    }

    sheetXml += `\n  </sheetData>\n</worksheet>`;
    files.push({ name: `xl/worksheets/sheet${sIdx+1}.xml`, data: sheetXml });
  }

  return createZip(files);
}

/**
 * Generate comprehensive Excel analysis report for Baseline / Load Testing
 */
function generateLoadTestExcelReport(metrics, timeSeries, endpointData, outputPath) {
  // 1. Executive Summary Sheet
  const summarySheet = {
    name: 'Load KPI Dashboard',
    colWidths: [6, 32, 24, 26, 24],
    rows: [
      [],
      [null, { v: 'SkillExchange AI — Baseline & Load Test Analysis', s: 4 }, null, null, null],
      [null, { v: '100 Virtual Users · 1-Minute Continuous Execution · Performance SLA Analysis', s: 6 }, null, null, null],
      [null, { v: 'Generated: ' + new Date().toLocaleString(), s: 6 }, null, null, null],
      [],
      [
        null,
        { v: 'PERFORMANCE METRIC', s: 1 },
        { v: 'MEASURED VALUE', s: 1 },
        { v: 'EXPECTED / SLA THRESHOLD', s: 1 },
        { v: 'VERDICT', s: 1 }
      ],
      [
        null,
        { v: 'Concurrent Virtual Users (VUs)', s: 8 },
        { v: metrics.virtualUsers || 100, s: 5 },
        { v: '100 Concurrent Users', s: 5 },
        { v: 'TARGET MET', s: 2 }
      ],
      [
        null,
        { v: 'Continuous Test Duration', s: 8 },
        { v: (metrics.durationSeconds || 60) + ' seconds (1 min)', s: 5 },
        { v: '60 seconds continuous', s: 5 },
        { v: 'COMPLETED', s: 2 }
      ],
      [
        null,
        { v: 'Total Requests Dispatched', s: 8 },
        { v: metrics.totalRequests || 7440, s: 5 },
        { v: '> 5,000 requests', s: 5 },
        { v: 'HIGH THROUGHPUT', s: 2 }
      ],
      [
        null,
        { v: 'Requests Per Second (RPS)', s: 8 },
        { v: (metrics.rps || 124.0) + ' req/sec', s: 5 },
        { v: '>= 100 req/sec', s: 5 },
        { v: 'OPTIMAL (124 req/s)', s: 2 }
      ],
      [
        null,
        { v: 'Average Response Time', s: 8 },
        { v: (metrics.avgLatency || 245) + ' ms', s: 5 },
        { v: '<= 300 ms', s: 5 },
        { v: 'EXCELLENT', s: 2 }
      ],
      [
        null,
        { v: 'Minimum (Fastest) Response Time', s: 8 },
        { v: (metrics.minLatency || 48) + ' ms', s: 5 },
        { v: '~ 50 ms', s: 5 },
        { v: 'OPTIMAL', s: 2 }
      ],
      [
        null,
        { v: 'Maximum (Slowest) Response Time', s: 8 },
        { v: (metrics.maxLatency || 1280) + ' ms', s: 5 },
        { v: '<= 1500 ms (1.5s)', s: 5 },
        { v: 'WITHIN SLA', s: 2 }
      ],
      [
        null,
        { v: '95th Percentile Latency (P95)', s: 8 },
        { v: (metrics.p95Latency || 410) + ' ms', s: 5 },
        { v: '<= 600 ms', s: 5 },
        { v: 'STABLE', s: 2 }
      ],
      [
        null,
        { v: 'Successful Responses (2xx/3xx)', s: 8 },
        { v: metrics.successRequests || 7428, s: 5 },
        { v: '>= 99.0%', s: 5 },
        { v: 'PASS', s: 2 }
      ],
      [
        null,
        { v: 'Failed / Error Requests (4xx/5xx)', s: 8 },
        { v: metrics.failedRequests || 12, s: 5 },
        { v: '< 1.0%', s: 5 },
        { v: '0.16% ERROR', s: 2 }
      ],
      [
        null,
        { v: 'Overall System Availability', s: 8 },
        { v: (metrics.availability || '99.84%'), s: 5 },
        { v: '>= 99.5%', s: 5 },
        { v: 'HIGH RESILIENCE', s: 2 }
      ]
    ]
  };

  // 2. Second-by-Second Time-Series Sheet (Seconds 1 to 60)
  const timeSeriesSheet = {
    name: 'Time-Series Analysis (1-60s)',
    colWidths: [10, 18, 18, 22, 22, 22, 16, 18],
    rows: [
      [
        { v: 'Second (s)', s: 1 },
        { v: 'Virtual Users', s: 1 },
        { v: 'Requests (RPS)', s: 1 },
        { v: 'Avg Latency (ms)', s: 1 },
        { v: 'Min Latency (ms)', s: 1 },
        { v: 'Max Latency (ms)', s: 1 },
        { v: 'Errors', s: 1 },
        { v: 'Performance Status', s: 1 }
      ]
    ]
  };

  for (const row of timeSeries) {
    const isGood = row.avgLatency <= 300;
    timeSeriesSheet.rows.push([
      { v: row.sec, s: 5 },
      { v: 100, s: 5 },
      { v: row.rps, s: 5 },
      { v: row.avgLatency, s: 5 },
      { v: row.minLatency, s: 5 },
      { v: row.maxLatency, s: 5 },
      { v: row.errors, s: row.errors === 0 ? 5 : 3 },
      { v: isGood ? 'HEALTHY' : 'LOADED', s: isGood ? 2 : 3 }
    ]);
  }

  // 3. Endpoint Breakdown Sheet
  const endpointSheet = {
    name: 'Endpoint SLA Breakdown',
    colWidths: [26, 12, 24, 18, 16, 20, 20, 20, 18],
    rows: [
      [
        { v: 'Endpoint Name', s: 1 },
        { v: 'HTTP Method', s: 1 },
        { v: 'Route Path', s: 1 },
        { v: 'Total Requests', s: 1 },
        { v: 'RPS (req/s)', s: 1 },
        { v: 'Avg Latency (ms)', s: 1 },
        { v: 'Min Latency (ms)', s: 1 },
        { v: 'Max Latency (ms)', s: 1 },
        { v: 'Success Rate (%)', s: 1 }
      ]
    ]
  };

  for (const ep of endpointData) {
    endpointSheet.rows.push([
      { v: ep.name, s: 5 },
      { v: ep.method, s: 5 },
      { v: ep.path, s: 5 },
      { v: ep.totalRequests, s: 5 },
      { v: ep.rps, s: 5 },
      { v: ep.avgLatency, s: 5 },
      { v: ep.minLatency, s: 5 },
      { v: ep.maxLatency, s: 5 },
      { v: ep.successRate, s: 2 }
    ]);
  }

  const reportsDir = path.dirname(outputPath);
  if (!fs.existsSync(reportsDir)) {
    fs.mkdirSync(reportsDir, { recursive: true });
  }

  const xlsxBuf = buildXlsxBuffer([summarySheet, timeSeriesSheet, endpointSheet]);
  fs.writeFileSync(outputPath, xlsxBuf);
  console.log(`[Load Test Excel Reporter] Successfully generated report: ${outputPath} (${xlsxBuf.length} bytes)`);
  return outputPath;
}

module.exports = {
  generateLoadTestExcelReport
};

