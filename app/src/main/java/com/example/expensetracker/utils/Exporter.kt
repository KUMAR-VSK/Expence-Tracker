package com.example.expensetracker.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.expensetracker.domain.model.Expense
import com.example.expensetracker.domain.model.TransactionType
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Exporter {

    fun exportToCsv(expenses: List<Expense>, dateFormat: String): String {
        val sb = StringBuilder()
        // CSV Headers
        sb.append("ID,Date,Time,Amount,Type,Category,Payment Method,Notes,Location,Tags\n")
        expenses.forEach { e ->
            val dateStr = DateUtils.formatEpoch(e.dateLong, dateFormat)
            val cleanNotes = e.notes.replace("\"", "\"\"")
            val cleanTags = e.tags.joinToString(";").replace("\"", "\"\"")
            sb.append("${e.id},\"$dateStr\",\"${e.timeString}\",${e.amount},\"${e.type.name}\",\"${e.category?.name ?: ""}\",\"${e.paymentMethod?.name ?: ""}\",\"$cleanNotes\",\"${e.location ?: ""}\",\"$cleanTags\"\n")
        }
        return sb.toString()
    }

    fun exportToExcelXml(expenses: List<Expense>, dateFormat: String): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\"?>\n")
        sb.append("<?mso-application progid=\"Excel.Sheet\"?>\n")
        sb.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n")
        sb.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n")
        sb.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n")
        sb.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\"\n")
        sb.append(" xmlns:html=\"http://www.w3.org/TR/REC-html40\">\n")
        
        // Styles
        sb.append(" <Styles>\n")
        sb.append("  <Style ss:ID=\"Header\">\n")
        sb.append("   <Font ss:Bold=\"1\" ss:Color=\"#FFFFFF\"/>\n")
        sb.append("   <Interior ss:Color=\"#6200EE\" ss:Pattern=\"Solid\"/>\n")
        sb.append("   <Alignment ss:Horizontal=\"Center\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"Income\">\n")
        sb.append("   <Font ss:Color=\"#00875A\"/>\n")
        sb.append("  </Style>\n")
        sb.append("  <Style ss:ID=\"Expense\">\n")
        sb.append("   <Font ss:Color=\"#DE350B\"/>\n")
        sb.append("  </Style>\n")
        sb.append(" </Styles>\n")

        sb.append(" <Worksheet ss:Name=\"Expenses\">\n")
        sb.append("  <Table>\n")
        
        // Headers
        sb.append("   <Row ss:Height=\"20\">\n")
        val headers = listOf("ID", "Date", "Time", "Amount", "Type", "Category", "Payment Method", "Notes", "Location", "Tags")
        headers.forEach { h ->
            sb.append("    <Cell ss:StyleID=\"Header\"><Data ss:Type=\"String\">$h</Data></Cell>\n")
        }
        sb.append("   </Row>\n")

        // Rows
        expenses.forEach { e ->
            val dateStr = DateUtils.formatEpoch(e.dateLong, dateFormat)
            val styleId = if (e.type == TransactionType.INCOME) "Income" else "Expense"
            val tagsStr = e.tags.joinToString(", ")
            
            sb.append("   <Row>\n")
            sb.append("    <Cell><Data ss:Type=\"Number\">${e.id}</Data></Cell>\n")
            sb.append("    <Cell><Data ss:Type=\"String\">$dateStr</Data></Cell>\n")
            sb.append("    <Cell><Data ss:Type=\"String\">${e.timeString}</Data></Cell>\n")
            sb.append("    <Cell ss:StyleID=\"$styleId\"><Data ss:Type=\"Number\">${e.amount}</Data></Cell>\n")
            sb.append("    <Cell><Data ss:Type=\"String\">${e.type.name}</Data></Cell>\n")
            sb.append("    <Cell><Data ss:Type=\"String\">${e.category?.name ?: ""}</Data></Cell>\n")
            sb.append("    <Cell><Data ss:Type=\"String\">${e.paymentMethod?.name ?: ""}</Data></Cell>\n")
            sb.append("    <Cell><Data ss:Type=\"String\">${e.notes}</Data></Cell>\n")
            sb.append("    <Cell><Data ss:Type=\"String\">${e.location ?: ""}</Data></Cell>\n")
            sb.append("    <Cell><Data ss:Type=\"String\">$tagsStr</Data></Cell>\n")
            sb.append("   </Row>\n")
        }

        sb.append("  </Table>\n")
        sb.append(" </Worksheet>\n")
        sb.append("</Workbook>\n")
        
        return sb.toString()
    }

    fun exportToPdf(context: Context, expenses: List<Expense>, dateFormat: String, outputStream: OutputStream) {
        val pdf = PdfDocument()
        val headerPaint = Paint().apply {
            color = Color.parseColor("#1A237E") // Premium Deep Blue
            style = Paint.Style.FILL
        }
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9f
            isAntiAlias = true
        }
        val boldPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 16f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val subTitlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 9f
            isAntiAlias = true
        }
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 0.5f
        }

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        var page = pdf.startPage(pageInfo)
        var canvas = page.canvas

        // Header Banner
        canvas.drawRect(0f, 0f, 595f, 75f, headerPaint)
        canvas.drawText("EXPENSE TRACKER REPORT", 24f, 36f, titlePaint)
        canvas.drawText("Generated on: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())}", 24f, 56f, subTitlePaint)

        var y = 110f
        
        // Table Headers
        canvas.drawText("DATE", 24f, y, boldPaint)
        canvas.drawText("CATEGORY", 100f, y, boldPaint)
        canvas.drawText("TYPE", 220f, y, boldPaint)
        canvas.drawText("AMOUNT", 290f, y, boldPaint)
        canvas.drawText("NOTES / PAY METHOD", 380f, y, boldPaint)
        y += 8f
        canvas.drawLine(24f, y, 570f, y, boldPaint)
        y += 18f

        expenses.forEach { e ->
            if (y > 800f) {
                pdf.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                page = pdf.startPage(pageInfo)
                canvas = page.canvas
                
                // Draw headers on new page too
                canvas.drawRect(0f, 0f, 595f, 40f, headerPaint)
                canvas.drawText("EXPENSE TRACKER REPORT - PAGE $pageNumber", 24f, 24f, titlePaint)
                
                y = 70f
                canvas.drawText("DATE", 24f, y, boldPaint)
                canvas.drawText("CATEGORY", 100f, y, boldPaint)
                canvas.drawText("TYPE", 220f, y, boldPaint)
                canvas.drawText("AMOUNT", 290f, y, boldPaint)
                canvas.drawText("NOTES / PAY METHOD", 380f, y, boldPaint)
                y += 8f
                canvas.drawLine(24f, y, 570f, y, boldPaint)
                y += 18f
            }

            val dateStr = DateUtils.formatEpoch(e.dateLong, dateFormat)
            canvas.drawText(dateStr, 24f, y, textPaint)
            canvas.drawText(e.category?.name ?: "Others", 100f, y, textPaint)
            
            val typeText = e.type.name
            val typeColor = if (e.type == TransactionType.INCOME) Color.parseColor("#00875A") else Color.parseColor("#DE350B")
            val typePaint = Paint(textPaint).apply { color = typeColor }
            canvas.drawText(typeText, 220f, y, typePaint)
            
            val amountText = String.format(Locale.getDefault(), "%.2f", e.amount)
            canvas.drawText(amountText, 290f, y, textPaint)
            
            val methodText = e.paymentMethod?.name ?: ""
            val noteAndMethod = if (e.notes.isBlank()) methodText else "${e.notes.take(22)} ($methodText)"
            canvas.drawText(noteAndMethod, 380f, y, textPaint)

            y += 10f
            canvas.drawLine(24f, y, 570f, y, linePaint)
            y += 18f
        }

        pdf.finishPage(page)
        pdf.writeTo(outputStream)
        pdf.close()
    }
}
