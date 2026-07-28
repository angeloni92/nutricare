package com.angeloni.nutricare.ui.dialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPBdr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;

import com.angeloni.nutricare.dto.AnthropometryDto;
import com.angeloni.nutricare.dto.CircumferenceDto;
import com.angeloni.nutricare.dto.ClientDto;
import com.angeloni.nutricare.dto.FoldDto;
import com.angeloni.nutricare.service.I18nService;

public class ExportUtils {

    private static final DateTimeFormatter EXPORT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ─── PUBLIC API ───────────────────────────────────────────────────────────

    public static void writePdf(File file, String clientName, String dietText, I18nService i18n) throws IOException {
        String date = LocalDateTime.now().format(EXPORT_FMT);
        PdfWriter pdf = new PdfWriter(clientName, date, i18n);
        pdf.render(dietText);
        pdf.save(file);
    }

    public static void writeDocx(File file, String clientName, String dietText, I18nService i18n) throws IOException {
        String date = LocalDateTime.now().format(EXPORT_FMT);
        try (XWPFDocument doc = new XWPFDocument()) {
            applyPageMargins(doc);
            addDocxCoverBlock(doc, clientName, date, i18n);
            addDocxContent(doc, dietText);
            try (FileOutputStream out = new FileOutputStream(file)) {
                doc.write(out);
            }
        }
    }

    public static void writeXlsxClients(File file, List<ClientDto> clients,
            Function<Long, List<AnthropometryDto>> visitsLoader, I18nService i18n) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet(i18n.t("export.sheet.clients"));
            XlsxStyles styles = new XlsxStyles(wb);

            String[] headers = {
                i18n.t("export.col.name"),       i18n.t("export.col.surname"),
                i18n.t("export.col.age"),         i18n.t("export.col.country"),
                i18n.t("export.col.last.visit"),  i18n.t("export.col.height"),
                i18n.t("export.col.weight"),      i18n.t("export.col.bmi"),
                i18n.t("export.col.pectoral"),    i18n.t("export.col.axillary"),
                i18n.t("export.col.suprailiac"),  i18n.t("export.col.abdominal"),
                i18n.t("export.col.triceps"),     i18n.t("export.col.subscapular"),
                i18n.t("export.col.thigh.fold"),  i18n.t("export.col.chest.circ"),
                i18n.t("export.col.arm.circ"),    i18n.t("export.col.waist.circ"),
                i18n.t("export.col.hip.circ"),    i18n.t("export.col.thigh.circ")
            };

            // Row 0: Title
            XSSFRow titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(36f);
            XSSFCell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(i18n.t("export.title"));
            titleCell.setCellStyle(styles.title);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

            // Row 1: Subtitle
            XSSFRow subtitleRow = sheet.createRow(1);
            subtitleRow.setHeightInPoints(22f);
            XSSFCell subtitleCell = subtitleRow.createCell(0);
            subtitleCell.setCellValue(i18n.t("export.subtitle",
                    LocalDateTime.now().format(EXPORT_FMT), clients.size()));
            subtitleCell.setCellStyle(styles.subtitle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.length - 1));

            // Row 2: spacer
            XSSFRow spacerRow = sheet.createRow(2);
            spacerRow.setHeightInPoints(8f);
            for (int i = 0; i < headers.length; i++) {
                spacerRow.createCell(i).setCellStyle(styles.spacer);
            }

            // Row 3: Headers
            XSSFRow headerRow = sheet.createRow(3);
            headerRow.setHeightInPoints(28f);
            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styles.header);
            }

            sheet.createFreezePane(0, 4);
            sheet.setAutoFilter(new CellRangeAddress(3, 3 + Math.max(clients.size(), 1), 0, headers.length - 1));

            DateTimeFormatter visFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (int r = 0; r < clients.size(); r++) {
                ClientDto c = clients.get(r);
                XSSFRow row = sheet.createRow(r + 4);
                row.setHeightInPoints(20f);
                boolean even = r % 2 == 0;
                XSSFCellStyle str = even ? styles.strEven : styles.strOdd;
                XSSFCellStyle num = even ? styles.numEven : styles.numOdd;

                setStr(row, 0, c.getName(),    str);
                setStr(row, 1, c.getSurname(), str);
                if (c.getAge() != null) setNum(row, 2, c.getAge(), num); else setStr(row, 2, "-", str);
                setStr(row, 3, c.getCountry(), str);

                try {
                    List<AnthropometryDto> visits = visitsLoader.apply(c.getId());
                    if (!visits.isEmpty()) {
                        AnthropometryDto v = visits.get(0);
                        if (v.getCreatedAt() != null) setStr(row, 4, v.getCreatedAt().format(visFmt), str);
                        if (v.getHeight() != null) setNum(row, 5, v.getHeight(), num);
                        if (v.getWeight() != null) setNum(row, 6, v.getWeight(), num);
                        if (v.getHeight() != null && v.getWeight() != null && v.getHeight() > 0)
                            setNum(row, 7, v.getWeight() / Math.pow(v.getHeight() / 100.0, 2), num);
                        FoldDto f = v.getFold();
                        if (f != null) {
                            if (f.getPectoral()      != null) setNum(row, 8,  f.getPectoral(), num);
                            if (f.getAxillary()      != null) setNum(row, 9,  f.getAxillary(), num);
                            if (f.getSuprailiac()    != null) setNum(row, 10, f.getSuprailiac(), num);
                            if (f.getAbdominal()     != null) setNum(row, 11, f.getAbdominal(), num);
                            if (f.getTriceps()       != null) setNum(row, 12, f.getTriceps(), num);
                            if (f.getSubscapolaris() != null) setNum(row, 13, f.getSubscapolaris(), num);
                            if (f.getThigh()         != null) setNum(row, 14, f.getThigh(), num);
                        }
                        CircumferenceDto circ = v.getCircumference();
                        if (circ != null) {
                            if (circ.getChest()  != null) setNum(row, 15, circ.getChest(), num);
                            if (circ.getArm()    != null) setNum(row, 16, circ.getArm(), num);
                            if (circ.getWaist()  != null) setNum(row, 17, circ.getWaist(), num);
                            if (circ.getHip()    != null) setNum(row, 18, circ.getHip(), num);
                            if (circ.getThigh()  != null) setNum(row, 19, circ.getThigh(), num);
                        }
                    }
                } catch (Exception ignored) {}
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 768, 6000));
            }

            try (FileOutputStream out = new FileOutputStream(file)) { wb.write(out); }
        }
    }

    public static String safeFilename(String name) {
        if (name == null) return "export";
        return name.replaceAll("[^a-zA-Z0-9._\\- ]", "_").trim();
    }

    // ─── WORD HELPERS ─────────────────────────────────────────────────────────

    private static void applyPageMargins(XWPFDocument doc) {
        CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                ? doc.getDocument().getBody().getSectPr()
                : doc.getDocument().getBody().addNewSectPr();
        CTPageMar m = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
        m.setTop(BigInteger.valueOf(1080));
        m.setBottom(BigInteger.valueOf(1080));
        m.setLeft(BigInteger.valueOf(1440));
        m.setRight(BigInteger.valueOf(1440));
    }

    private static void addDocxCoverBlock(XWPFDocument doc, String clientName, String date, I18nService i18n) {
        XWPFParagraph titlePara = doc.createParagraph();
        titlePara.setAlignment(ParagraphAlignment.CENTER);
        titlePara.setSpacingBefore(0);
        titlePara.setSpacingAfter(0);
        setParaBg(titlePara, "131C2E");
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(i18n.t("export.diet.title"));
        titleRun.setBold(true);
        titleRun.setFontSize(22);
        titleRun.setColor("FFFFFF");
        titleRun.setFontFamily("Calibri");
        titleRun.addBreak();
        XWPFRun subRun = titlePara.createRun();
        subRun.setText(i18n.t("export.diet.subtitle", clientName));
        subRun.setFontSize(11);
        subRun.setColor("BFD0E8");
        subRun.setFontFamily("Calibri");

        XWPFParagraph spacer1 = doc.createParagraph();
        spacer1.setSpacingAfter(0);

        XWPFParagraph infoPara = doc.createParagraph();
        infoPara.setAlignment(ParagraphAlignment.LEFT);
        infoPara.setSpacingBefore(120);
        infoPara.setSpacingAfter(120);
        infoPara.setIndentationLeft(200);
        setParaBg(infoPara, "EFF6FF");
        setParaBorder(infoPara, "BFDBFE", 4);
        XWPFRun infoLbl = infoPara.createRun();
        infoLbl.setFontFamily("Calibri"); infoLbl.setFontSize(11);
        infoLbl.setColor("1E3A5F"); infoLbl.setBold(true);
        infoLbl.setText(i18n.t("export.diet.client.label"));
        XWPFRun infoVal = infoPara.createRun();
        infoVal.setFontFamily("Calibri"); infoVal.setFontSize(11);
        infoVal.setColor("374151"); infoVal.setText(clientName);
        infoVal.addBreak();
        XWPFRun dateLbl = infoPara.createRun();
        dateLbl.setFontFamily("Calibri"); dateLbl.setFontSize(11);
        dateLbl.setColor("1E3A5F"); dateLbl.setBold(true);
        dateLbl.setText(i18n.t("export.diet.date.label"));
        XWPFRun dateVal = infoPara.createRun();
        dateVal.setFontFamily("Calibri"); dateVal.setFontSize(11);
        dateVal.setColor("374151"); dateVal.setText(date);

        XWPFParagraph spacer2 = doc.createParagraph();
        spacer2.setSpacingAfter(240);
    }

    private static void addDocxContent(XWPFDocument doc, String dietText) {
        if (dietText == null) return;
        for (String line : dietText.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                XWPFParagraph empty = doc.createParagraph();
                empty.setSpacingAfter(80);
                continue;
            }
            boolean isDayHdr     = isDayHeader(trimmed);
            boolean isCalories   = !isDayHdr && isCaloriesLine(trimmed);
            boolean isSectionHdr = !isDayHdr && !isCalories && isHeader(trimmed);
            boolean isBullet     = !isDayHdr && !isSectionHdr && !isCalories && isBulletItem(trimmed);
            if (isDayHdr) {
                XWPFParagraph p = doc.createParagraph();
                p.setSpacingBefore(480); p.setSpacingAfter(120);
                setParaBg(p, "131C2E");
                XWPFRun r = p.createRun();
                r.setText(trimmed); r.setBold(true); r.setFontSize(13);
                r.setColor("FFFFFF"); r.setFontFamily("Calibri");
            } else if (isCalories) {
                XWPFParagraph p = doc.createParagraph();
                p.setSpacingBefore(100); p.setSpacingAfter(80);
                p.setIndentationLeft(200);
                XWPFRun r = p.createRun();
                r.setText(trimmed); r.setBold(true); r.setFontSize(11);
                r.setColor("059669"); r.setFontFamily("Calibri");
            } else if (isSectionHdr) {
                XWPFParagraph p = doc.createParagraph();
                p.setSpacingBefore(360); p.setSpacingAfter(80);
                addSectionBorder(p, "4F46E5");
                XWPFRun r = p.createRun();
                r.setText(trimmed); r.setBold(true); r.setFontSize(12);
                r.setColor("3730A3"); r.setFontFamily("Calibri");
            } else if (isBullet) {
                XWPFParagraph p = doc.createParagraph();
                p.setSpacingAfter(60);
                p.setIndentationLeft(400);
                XWPFRun r = p.createRun();
                r.setText(trimmed); r.setFontSize(11);
                r.setColor("374151"); r.setFontFamily("Calibri");
            } else {
                XWPFParagraph p = doc.createParagraph();
                p.setSpacingAfter(60);
                XWPFRun r = p.createRun();
                r.setText(trimmed); r.setFontSize(11);
                r.setColor("374151"); r.setFontFamily("Calibri");
            }
        }
    }

    private static void setParaBg(XWPFParagraph para, String hexFill) {
        CTPPr pPr = para.getCTP().isSetPPr() ? para.getCTP().getPPr() : para.getCTP().addNewPPr();
        CTShd shd = pPr.isSetShd() ? pPr.getShd() : pPr.addNewShd();
        shd.setVal(STShd.CLEAR);
        shd.setColor("auto");
        shd.setFill(hexFill);
    }

    private static void setParaBorder(XWPFParagraph para, String hexColor, int szHalfPts) {
        CTPPr pPr = para.getCTP().isSetPPr() ? para.getCTP().getPPr() : para.getCTP().addNewPPr();
        CTPBdr bdr = pPr.isSetPBdr() ? pPr.getPBdr() : pPr.addNewPBdr();
        applyBorderSide(bdr.isSetTop()    ? bdr.getTop()    : bdr.addNewTop(),    hexColor, szHalfPts);
        applyBorderSide(bdr.isSetBottom() ? bdr.getBottom() : bdr.addNewBottom(), hexColor, szHalfPts);
        applyBorderSide(bdr.isSetLeft()   ? bdr.getLeft()   : bdr.addNewLeft(),   hexColor, szHalfPts);
        applyBorderSide(bdr.isSetRight()  ? bdr.getRight()  : bdr.addNewRight(),  hexColor, szHalfPts);
    }

    private static void addSectionBorder(XWPFParagraph para, String hexColor) {
        CTPPr pPr = para.getCTP().isSetPPr() ? para.getCTP().getPPr() : para.getCTP().addNewPPr();
        CTPBdr bdr = pPr.isSetPBdr() ? pPr.getPBdr() : pPr.addNewPBdr();
        applyBorderSide(bdr.isSetBottom() ? bdr.getBottom() : bdr.addNewBottom(), hexColor, 4);
    }

    private static void applyBorderSide(CTBorder border, String hexColor, int szHalfPts) {
        border.setVal(STBorder.SINGLE);
        border.setColor(hexColor);
        border.setSz(BigInteger.valueOf(szHalfPts));
        border.setSpace(BigInteger.valueOf(4));
    }

    // ─── EXCEL HELPERS ────────────────────────────────────────────────────────

    private static void setStr(XSSFRow row, int col, String val, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(col);
        cell.setCellValue(val != null ? val : "-");
        cell.setCellStyle(style);
    }

    private static void setNum(XSSFRow row, int col, double val, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(col);
        cell.setCellValue(val);
        cell.setCellStyle(style);
    }

    private static class XlsxStyles {
        final XSSFCellStyle title, subtitle, spacer, header;
        final XSSFCellStyle strEven, strOdd, numEven, numOdd;

        XlsxStyles(XSSFWorkbook wb) {
            XSSFColor navyColor     = new XSSFColor(new byte[]{0x1e, 0x29, 0x3b}, null);
            XSSFColor whiteColor    = new XSSFColor(new byte[]{(byte)0xff, (byte)0xff, (byte)0xff}, null);
            XSSFColor indigoColor   = new XSSFColor(new byte[]{0x4f, 0x46, (byte)0xe5}, null);
            XSSFColor grayTextColor = new XSSFColor(new byte[]{(byte)0x94, (byte)0xa3, (byte)0xb8}, null);
            XSSFColor altRowColor   = new XSSFColor(new byte[]{(byte)0xf1, (byte)0xf5, (byte)0xf9}, null);
            XSSFColor bodyTextColor = new XSSFColor(new byte[]{0x37, 0x41, 0x51}, null);
            XSSFColor slate700      = new XSSFColor(new byte[]{0x33, 0x41, 0x55}, null);
            XSSFColor borderColor   = new XSSFColor(new byte[]{(byte)0xe2, (byte)0xe8, (byte)0xf0}, null);

            title = wb.createCellStyle();
            title.setFillForegroundColor(navyColor);
            title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            title.setAlignment(HorizontalAlignment.CENTER);
            title.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont tf = wb.createFont();
            tf.setBold(true); tf.setFontHeightInPoints((short)15);
            tf.setFontName("Calibri"); tf.setColor(whiteColor);
            title.setFont(tf);

            subtitle = wb.createCellStyle();
            subtitle.setFillForegroundColor(slate700);
            subtitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            subtitle.setAlignment(HorizontalAlignment.CENTER);
            subtitle.setVerticalAlignment(VerticalAlignment.CENTER);
            XSSFFont sf = wb.createFont();
            sf.setFontHeightInPoints((short)10); sf.setFontName("Calibri"); sf.setColor(grayTextColor);
            subtitle.setFont(sf);

            spacer = wb.createCellStyle();
            spacer.setFillForegroundColor(borderColor);
            spacer.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            header = wb.createCellStyle();
            header.setFillForegroundColor(indigoColor);
            header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            header.setAlignment(HorizontalAlignment.CENTER);
            header.setVerticalAlignment(VerticalAlignment.CENTER);
            header.setBorderBottom(BorderStyle.MEDIUM);
            header.setBorderTop(BorderStyle.MEDIUM);
            header.setBorderLeft(BorderStyle.THIN);
            header.setBorderRight(BorderStyle.THIN);
            XSSFFont hf = wb.createFont();
            hf.setBold(true); hf.setFontHeightInPoints((short)10);
            hf.setFontName("Calibri"); hf.setColor(whiteColor);
            header.setFont(hf);

            strEven = makeDataStyle(wb, bodyTextColor, null,        false);
            strOdd  = makeDataStyle(wb, bodyTextColor, altRowColor, false);
            numEven = makeDataStyle(wb, bodyTextColor, null,        true);
            numOdd  = makeDataStyle(wb, bodyTextColor, altRowColor, true);
        }

        private XSSFCellStyle makeDataStyle(XSSFWorkbook wb, XSSFColor textColor,
                XSSFColor bgColor, boolean rightAlign) {
            XSSFCellStyle style = wb.createCellStyle();
            if (bgColor != null) {
                style.setFillForegroundColor(bgColor);
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            }
            style.setAlignment(rightAlign ? HorizontalAlignment.RIGHT : HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            if (rightAlign) style.setDataFormat(wb.createDataFormat().getFormat("0.0#"));
            XSSFFont f = wb.createFont();
            f.setFontName("Calibri"); f.setFontHeightInPoints((short)10); f.setColor(textColor);
            style.setFont(f);
            style.setIndention((short)1);
            return style;
        }
    }

    // ─── PDF WRITER (pure Java, no external libs) ─────────────────────────────

    private static final class PdfWriter {
        private static final float PW = 595.28f, PH = 841.89f;
        private static final float ML = 52f, MT = 44f, MB = 52f;
        private static final float USABLE = PW - ML - 52f;

        private static final int[] C_NAVY        = {19,  28,  46};
        private static final int[] C_INDIGO       = {79,  70, 229};
        private static final int[] C_LIGHT_INDIGO = {188, 186, 245};
        private static final int[] C_WHITE        = {255, 255, 255};
        private static final int[] C_BODY         = {55,  65,  81};
        private static final int[] C_SECTION      = {55,  48, 163};
        private static final int[] C_GRAY         = {148, 163, 184};
        private static final int[] C_LTBLUE       = {191, 208, 232};
        private static final int[] C_BORDER       = {226, 232, 240};
        private static final int[] C_GREEN        = {16,  185, 129};

        private static final float BOX_H   = 80f;
        private static final float BODY_FS = 10.5f;
        private static final float HDR_FS  = 11.5f;
        private static final float BODY_LH = 14.5f;
        private static final float HDR_LH  = 16.5f;

        private final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        private final List<Integer> xrefOffsets = new ArrayList<>();
        private int objCount = 0;
        private final List<byte[]> pageStreams = new ArrayList<>();
        private ByteArrayOutputStream currentStream;
        private final String clientName, date;
        private final I18nService i18n;
        private float yPos;

        PdfWriter(String clientName, String date, I18nService i18n) {
            this.clientName = clientName;
            this.date = date;
            this.i18n = i18n;
        }

        void render(String dietText) {
            startPage();
            drawFirstPageHeader();
            yPos = PH - MT - BOX_H - 48f;
            for (String rawLine : (dietText != null ? dietText : "").split("\n")) {
                String line = sanitize(rawLine.trim());
                if (line.isEmpty()) { yPos -= 5f; continue; }
                boolean isDayHdr   = isDayHeader(rawLine.trim());
                boolean isCalories = !isDayHdr && isCaloriesLine(rawLine.trim());
                boolean isHdr      = !isDayHdr && !isCalories && isHeader(rawLine.trim());
                boolean isBullet   = !isDayHdr && !isHdr && !isCalories && isBulletItem(line);
                if (isDayHdr) {
                    if (yPos < MB + HDR_LH + 36) { endPage(); startPage(); drawRunningHeader(); }
                    yPos -= 14f;
                    emitFilledRect(ML - 12, yPos - 6, USABLE + 24, HDR_LH + 10, C_NAVY);
                    emitText(ML, yPos, line, "F2", HDR_FS + 1f, C_WHITE);
                    yPos -= (HDR_LH + 8f);
                } else if (isCalories) {
                    if (yPos < MB + BODY_LH) { endPage(); startPage(); drawRunningHeader(); }
                    for (String wl : wrapChars(line, 93)) {
                        if (yPos < MB + BODY_LH) { endPage(); startPage(); drawRunningHeader(); }
                        emitText(ML + 8, yPos, wl, "F2", BODY_FS, C_GREEN);
                        yPos -= BODY_LH;
                    }
                } else if (isHdr) {
                    if (yPos < MB + HDR_LH + 24) { endPage(); startPage(); drawRunningHeader(); }
                    yPos -= 10f;
                    drawLine(yPos + 2, C_LIGHT_INDIGO, 0.5f);
                    yPos -= 12f;
                    for (String wl : wrapChars(line, 88)) {
                        if (yPos < MB + BODY_LH) { endPage(); startPage(); drawRunningHeader(); }
                        emitText(ML, yPos, wl, "F2", HDR_FS, C_SECTION);
                        yPos -= HDR_LH;
                    }
                } else {
                    if (yPos < MB + BODY_LH) { endPage(); startPage(); drawRunningHeader(); }
                    float xOff = isBullet ? ML + 14 : ML;
                    for (String wl : wrapChars(line, isBullet ? 93 : 95)) {
                        if (yPos < MB + BODY_LH) { endPage(); startPage(); drawRunningHeader(); }
                        emitText(xOff, yPos, wl, "F1", BODY_FS, C_BODY);
                        yPos -= BODY_LH;
                    }
                }
            }
            endPage();
        }

        private void drawFirstPageHeader() {
            float boxY = PH - MT - BOX_H;
            emitFilledRect(ML - 12, boxY, USABLE + 24, BOX_H, C_NAVY);
            emitText(ML, boxY + BOX_H - 32f, sanitize(i18n.t("export.diet.title")), "F2", 16f, C_WHITE);
            emitText(ML, boxY + BOX_H - 52f,
                    sanitize(i18n.t("export.diet.subtitle.pdf", clientName)), "F1", 10f, C_LTBLUE);
            float infoY = boxY - 14f;
            emitText(ML, infoY, sanitize(i18n.t("export.diet.info.line",
                    date, clientName)), "F1", 9f, C_GRAY);
            drawLine(infoY - 6f, C_INDIGO, 0.7f);
        }

        private void drawRunningHeader() {
            float hY = PH - MT - 18f;
            drawLine(hY, C_INDIGO, 0.7f);
            emitText(ML, hY + 4f, sanitize(i18n.t("export.diet.running.header")), "F2", 8.5f, C_SECTION);
            String rt = sanitize(clientName);
            emitText(ML + USABLE - rt.length() * 4.8f, hY + 4f, rt, "F1", 8.5f, C_GRAY);
            yPos = hY - 14f;
        }

        private void drawFooter(int pageNum) {
            drawLine(MB + 14f, C_BORDER, 0.5f);
            emitText(ML, MB, sanitize(i18n.t("export.diet.footer")), "F1", 7.5f, C_GRAY);
            String pg = sanitize(i18n.t("export.diet.page", pageNum));
            emitText(ML + USABLE - pg.length() * 4.5f, MB, pg, "F1", 7.5f, C_GRAY);
        }

        private void startPage() { currentStream = new ByteArrayOutputStream(); }

        private void endPage() {
            drawFooter(pageStreams.size() + 1);
            pageStreams.add(currentStream.toByteArray());
            currentStream = null;
        }

        private void emitText(float x, float y, String text, String font, float size, int[] rgb) {
            emit("BT\n/" + font + " " + fmt(size) + " Tf\n"
                + fmt(rgb[0]/255f) + " " + fmt(rgb[1]/255f) + " " + fmt(rgb[2]/255f) + " rg\n"
                + fmt(x) + " " + fmt(y) + " Td\n"
                + "(" + pdfEscape(text) + ") Tj\nET\n");
        }

        private void emitFilledRect(float x, float y, float w, float h, int[] rgb) {
            emit(fmt(rgb[0]/255f) + " " + fmt(rgb[1]/255f) + " " + fmt(rgb[2]/255f) + " rg\n"
                + fmt(x) + " " + fmt(y) + " " + fmt(w) + " " + fmt(h) + " re f\n0 0 0 rg\n");
        }

        private void drawLine(float y, int[] rgb, float width) {
            emit(fmt(rgb[0]/255f) + " " + fmt(rgb[1]/255f) + " " + fmt(rgb[2]/255f) + " RG\n"
                + fmt(width) + " w\n"
                + fmt(ML - 2) + " " + fmt(y) + " m " + fmt(ML + USABLE + 2) + " " + fmt(y) + " l S\n"
                + "0 0 0 RG\n0.5 w\n");
        }

        private void emit(String s) {
            try { currentStream.write(s.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1)); }
            catch (IOException ignored) {}
        }

        void save(File file) throws IOException {
            writeBytes("%PDF-1.4\n".getBytes());
            int catalogId = nextObj();
            int pagesId   = nextObj();
            int font1Id   = writeFont("Helvetica");
            int font2Id   = writeFont("Helvetica-Bold");
            List<Integer> pageIds = new ArrayList<>();
            for (byte[] stream : pageStreams) {
                int contentId = nextObj();
                writeStreamObj(contentId, stream);
                int pageId = nextObj();
                writePageObj(pageId, pagesId, contentId, font1Id, font2Id);
                pageIds.add(pageId);
            }
            writeXrefAt(pagesId);
            writeln(pagesId + " 0 obj");
            writeln("<< /Type /Pages");
            StringBuilder kids = new StringBuilder("/Kids [");
            for (int id : pageIds) kids.append(id).append(" 0 R ");
            writeln(kids.append("]").toString());
            writeln("/Count " + pageIds.size());
            writeln(">>");
            writeln("endobj");
            writeXrefAt(catalogId);
            writeln(catalogId + " 0 obj");
            writeln("<< /Type /Catalog /Pages " + pagesId + " 0 R >>");
            writeln("endobj");
            int xrefPos = buf.size();
            writeln("xref");
            writeln("0 " + (objCount + 1));
            writeln("0000000000 65535 f ");
            for (int offset : xrefOffsets) writeln(String.format("%010d 00000 n ", offset));
            writeln("trailer");
            writeln("<< /Size " + (objCount + 1) + " /Root " + catalogId + " 0 R >>");
            writeln("startxref");
            writeln(String.valueOf(xrefPos));
            writeln("%%EOF");
            try (FileOutputStream out = new FileOutputStream(file)) { buf.writeTo(out); }
        }

        private int nextObj() { return ++objCount; }

        private void writeXrefAt(int id) {
            while (xrefOffsets.size() < id) xrefOffsets.add(0);
            xrefOffsets.set(id - 1, buf.size());
        }

        private void writeStreamObj(int id, byte[] content) {
            writeXrefAt(id);
            writeln(id + " 0 obj");
            writeln("<< /Length " + content.length + " >>");
            writeln("stream");
            writeBytes(content);
            writeln("\nendstream");
            writeln("endobj");
        }

        private void writePageObj(int id, int pagesId, int contentId, int f1, int f2) {
            writeXrefAt(id);
            writeln(id + " 0 obj");
            writeln("<< /Type /Page /Parent " + pagesId + " 0 R");
            writeln("/MediaBox [0 0 " + fmt(PW) + " " + fmt(PH) + "]");
            writeln("/Contents " + contentId + " 0 R");
            writeln("/Resources << /Font << /F1 " + f1 + " 0 R /F2 " + f2 + " 0 R >> >>");
            writeln(">>");
            writeln("endobj");
        }

        private int writeFont(String name) {
            int id = nextObj();
            writeXrefAt(id);
            writeln(id + " 0 obj");
            writeln("<< /Type /Font /Subtype /Type1 /BaseFont /" + name + " /Encoding /WinAnsiEncoding >>");
            writeln("endobj");
            return id;
        }

        private void writeln(String s) {
            writeBytes((s + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        }
        private void writeBytes(byte[] b) { try { buf.write(b); } catch (IOException ignored) {} }
        private static String fmt(float v) { return String.format(java.util.Locale.US, "%.2f", v); }
        private static String pdfEscape(String s) {
            return s.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
        }
    }

    // ─── SHARED HELPERS ───────────────────────────────────────────────────────

    private static final java.util.Set<String> DAY_NAMES_UPPER = java.util.Set.of(
        "LUNEDI", "MARTEDI", "MERCOLEDI", "GIOVEDI", "VENERDI", "SABATO", "DOMENICA",
        "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    );

    private static boolean isDayHeader(String line) {
        if (line == null) return false;
        String t = line.trim().replaceAll("[:\\-*\\s0-9]+$", "").trim();
        String normalized = t.toUpperCase()
            .replace("À","A").replace("È","E").replace("É","E")
            .replace("Ì","I").replace("Ò","O").replace("Ù","U");
        return DAY_NAMES_UPPER.contains(normalized);
    }

    private static boolean isCaloriesLine(String line) {
        if (line == null) return false;
        String upper = line.toUpperCase();
        return upper.contains("CALORIE TOTALI") || upper.contains("TOTAL CALORIES")
               || upper.contains("TOTALE CALORIE");
    }

    private static boolean isBulletItem(String line) {
        return line != null && (line.startsWith("-") || line.startsWith("*") || line.startsWith("•"));
    }

    private static boolean isHeader(String line) {
        if (line == null || line.length() <= 3) return false;
        String t = line.trim().replaceAll("[:\\-*]+$", "").trim();
        if (t.length() <= 3) return false;
        long upper  = t.chars().filter(Character::isUpperCase).count();
        long letter = t.chars().filter(Character::isLetter).count();
        return letter > 0 && (double) upper / letter > 0.65;
    }

    private static List<String> wrapChars(String text, int maxChars) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) { lines.add(""); return lines; }
        String[] words = text.split(" ");
        StringBuilder cur = new StringBuilder();
        for (String w : words) {
            if (w.isEmpty()) continue;
            if (cur.length() > 0 && cur.length() + 1 + w.length() > maxChars) {
                lines.add(cur.toString()); cur = new StringBuilder(w);
            } else {
                if (cur.length() > 0) cur.append(' ');
                cur.append(w);
            }
        }
        if (cur.length() > 0) lines.add(cur.toString());
        if (lines.isEmpty()) lines.add(text);
        return lines;
    }

    private static String sanitize(String text) {
        if (text == null) return "";
        return text
            .replace('‘', '\'').replace('’', '\'')
            .replace('“', '"').replace('”', '"')
            .replace('–', '-').replace('—', '-')
            .replace('•', '*').replace('·', '*')
            .replace('à', 'a').replace('è', 'e').replace('é', 'e')
            .replace('ì', 'i').replace('ò', 'o').replace('ù', 'u')
            .replace('À', 'A').replace('È', 'E').replace('É', 'E')
            .replace('Ì', 'I').replace('Ò', 'O').replace('Ù', 'U')
            .replace('á', 'a').replace('í', 'i').replace('ó', 'o').replace('ú', 'u')
            .replaceAll("[^\\x20-\\x7E\n\r\t]", " ");
    }
}
