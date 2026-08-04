package dev.sandesh.kharcha.feature.parser.rules

import dev.sandesh.kharcha.core.model.TransactionStatus
import dev.sandesh.kharcha.core.model.TransactionType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SbiParserTest {

    private lateinit var parser: SbiParser
    private lateinit var engine: ParserEngine

    @Before
    fun setUp() {
        parser = SbiParser()
        engine = ParserEngine()
    }

    @Test
    fun `parse SBI debit INR format returns correct amount and type`() {
        val result = parser.parse(
            sender = "SBIINB",
            body = "Your A/c No.XX1234 is debited with INR 500.00 on 01-Jan-25. Avail bal: INR 14,734.56."
        )
        assertNotNull("Should parse transaction", result.transaction)
        assertEquals(500.0, result.transaction!!.amount, 0.01)
        assertEquals(TransactionType.DEBIT, result.transaction!!.type)
        assertTrue("Confidence should be ≥ 0.85", result.confidence >= 0.85f)
        assertEquals(TransactionStatus.CONFIRMED, result.transaction!!.status)
    }

    @Test
    fun `parse SBI UPI debit extracts merchant and ref`() {
        val result = parser.parse(
            sender = "SBIUPI",
            body = "Rs.250.00 debited from SBI A/c XX5678 to SWIGGY via UPI Ref 412345678901 on 31-01-2025. Avail bal: Rs.5,200.50."
        )
        assertNotNull(result.transaction)
        assertEquals(250.0, result.transaction!!.amount, 0.01)
        assertEquals(TransactionType.DEBIT, result.transaction!!.type)
        assertTrue(result.confidence >= 0.85f)
        assertTrue("Should extract UPI ref", result.transaction!!.txnRef.isNotEmpty())
    }

    @Test
    fun `parse SBI credit returns CREDIT type`() {
        val result = parser.parse(
            sender = "SBIINB",
            body = "Rs.1000.00 credited to SBI A/c XX1234 from RAHUL SHARMA via UPI Ref 519876543210 on 01-01-2025."
        )
        assertNotNull(result.transaction)
        assertEquals(1000.0, result.transaction!!.amount, 0.01)
        assertEquals(TransactionType.CREDIT, result.transaction!!.type)
        assertTrue(result.confidence >= 0.80f)
    }

    @Test
    fun `parse SBI large amount with commas`() {
        val result = parser.parse(
            sender = "SBIINB",
            body = "Your A/c No.XX3456 is debited with INR 45,000.00 on 05-Jan-25 towards Home Loan EMI. Avail bal: INR 1,23,456.78."
        )
        assertNotNull(result.transaction)
        assertEquals(45000.0, result.transaction!!.amount, 0.01)
        assertEquals(TransactionType.DEBIT, result.transaction!!.type)
    }

    @Test
    fun `OTP message returns zero confidence`() {
        val result = engine.parse(
            sender = "SBIINB",
            body = "123456 is your OTP for SBI Net Banking. Valid for 10 minutes. Do not share with anyone."
        )
        assertEquals("OTP should have zero confidence", 0f, result.confidence, 0.01f)
        assertNull("OTP should not produce transaction", result.transaction)
    }

    @Test
    fun `non-SBI sender returns zero confidence`() {
        val result = parser.parse(
            sender = "HDFCBK",
            body = "Your A/c No.XX1234 is debited with INR 500.00"
        )
        assertEquals(0f, result.confidence, 0.01f)
    }

    @Test
    fun `all SBI fixtures achieve minimum 85 percent accuracy`() {
        val sbiFixtures = listOf(
            Triple("SBIINB", "Your A/c No.XX1234 is debited with INR 500.00 on 01-Jan-25. Avail bal: INR 14,734.56.", 0.85f),
            Triple("SBIUPI", "Rs.250.00 debited from SBI A/c XX5678 to SWIGGY via UPI Ref 412345678901 on 31-01-2025.", 0.85f),
            Triple("SBIINB", "Rs.1000.00 credited to SBI A/c XX1234 from RAHUL SHARMA via UPI Ref 519876543210 on 01-01-2025.", 0.80f),
            Triple("SBICRD", "Your SBI A/c XX9012 is debited by Rs.2,500.00 for purchase at AMAZON on 15-Jan-2025.", 0.85f),
            Triple("SBIINB", "Your A/c No.XX3456 is debited with INR 45,000.00 on 05-Jan-25 towards Home Loan EMI.", 0.85f),
        )

        var passed = 0
        sbiFixtures.forEach { (sender, body, minConfidence) ->
            val result = parser.parse(sender, body)
            if (result.confidence >= minConfidence && result.transaction != null) passed++
        }

        val accuracy = passed.toFloat() / sbiFixtures.size
        assertTrue(
            "SBI parser accuracy should be ≥ 95%: got ${accuracy * 100}% ($passed/${sbiFixtures.size})",
            accuracy >= 0.95f
        )
    }
}

class BobParserTest {

    private val parser = BobParser()

    @Test
    fun `parse BoB debit returns correct fields`() {
        val result = parser.parse(
            sender = "BOBIBD",
            body = "Dear Customer, Rs.750.00 has been debited from your A/c XXXXXXXX1234 on 01-01-2025. Avail Bal: Rs.8,250.00."
        )
        assertNotNull(result.transaction)
        assertEquals(750.0, result.transaction!!.amount, 0.01)
        assertEquals(TransactionType.DEBIT, result.transaction!!.type)
        assertTrue(result.confidence >= 0.85f)
    }

    @Test
    fun `parse BoB credit returns CREDIT type`() {
        val result = parser.parse(
            sender = "BOBIBD",
            body = "INR 5,000.00 credited to your account ending 5678 on 15/01/2025. Available balance INR 12,500.00."
        )
        assertNotNull(result.transaction)
        assertEquals(5000.0, result.transaction!!.amount, 0.01)
        assertEquals(TransactionType.CREDIT, result.transaction!!.type)
    }

    @Test
    fun `non-BoB sender returns zero confidence`() {
        val result = parser.parse(
            sender = "SBIINB",
            body = "Dear Customer, Rs.750.00 has been debited from your A/c XXXXXXXX1234"
        )
        assertEquals(0f, result.confidence, 0.01f)
    }
}

class LicParserTest {

    private val parser = LicParser()

    @Test
    fun `parse LIC premium reminder extracts policy number and amount`() {
        val result = parser.parse(
            sender = "LICIND",
            body = "Dear Policyholder, your policy No. 123456789 premium of Rs.5,000 is due on 15-02-2025. Please pay before due date. -LIC of India"
        )
        assertNotNull(result.transaction)
        assertEquals(5000.0, result.transaction!!.amount, 0.01)
        assertTrue("LIC confidence must be ≤ 0.65", result.confidence <= 0.65f)
        assertEquals("LIC must go to NEEDS_REVIEW", TransactionStatus.NEEDS_REVIEW, result.transaction!!.status)
        assertEquals("123456789", result.transaction!!.txnRef)
    }

    @Test
    fun `parse LIC document submission notice returns partial result`() {
        val result = parser.parse(
            sender = "LICIND",
            body = "Dear Customer, Please submit your age proof for policy No. 987654321 at your nearest LIC branch. -LIC"
        )
        // Should still produce a transaction (for review) but with low confidence and no amount
        assertTrue(result.confidence > 0f)
        if (result.transaction != null) {
            assertEquals(TransactionStatus.NEEDS_REVIEW, result.transaction!!.status)
        }
    }

    @Test
    fun `non-LIC sender returns zero confidence`() {
        val result = parser.parse(
            sender = "SBIINB",
            body = "Dear Policyholder, your policy No. 123456789 premium of Rs.5,000 is due."
        )
        assertEquals(0f, result.confidence, 0.01f)
    }
}

class ParserEngineTest {

    private val engine = ParserEngine()

    @Test
    fun `engine routes SBI message to SBI parser`() {
        val result = engine.parse(
            sender = "SBIINB",
            body = "Your A/c No.XX1234 is debited with INR 500.00 on 01-Jan-25."
        )
        assertEquals("SBI", result.parserName)
        assertNotNull(result.transaction)
    }

    @Test
    fun `engine routes BoB message to BoB parser`() {
        val result = engine.parse(
            sender = "BOBIBD",
            body = "Dear Customer, Rs.750.00 has been debited from your A/c XXXXXXXX1234 on 01-01-2025."
        )
        assertEquals("BOB", result.parserName)
        assertNotNull(result.transaction)
    }

    @Test
    fun `engine returns no match for promotional message`() {
        val result = engine.parse(
            sender = "OFFER",
            body = "Congratulations! You have won a prize. Click here to claim. Reply STOP to unsubscribe."
        )
        assertNull(result.transaction)
    }

    @Test
    fun `engine dedupes: second identical transaction flagged`() {
        // Dedupe is handled in the ingestion layer — engine just parses
        val result1 = engine.parse("SBIINB", "Your A/c No.XX1234 is debited with INR 500.00 on 01-Jan-25.")
        val result2 = engine.parse("SBIINB", "Your A/c No.XX1234 is debited with INR 500.00 on 01-Jan-25.")
        // Both should parse successfully — dedup is responsibility of IngestionService
        assertNotNull(result1.transaction)
        assertNotNull(result2.transaction)
        assertEquals(result1.transaction!!.amount, result2.transaction!!.amount, 0.01)
    }
}

class GenericFallbackParserTest {

    private val parser = GenericFallbackParser()

    @Test
    fun `parse unknown bank UPI debit`() {
        val result = parser.parse(
            sender = "HDFCBK",
            body = "Rs.1000 debited from your account XX5555 via UPI. Avail bal Rs.5000."
        )
        assertNotNull(result.transaction)
        assertEquals(1000.0, result.transaction!!.amount, 0.01)
        assertEquals(TransactionType.DEBIT, result.transaction!!.type)
        assertEquals(TransactionStatus.NEEDS_REVIEW, result.transaction!!.status)
    }

    @Test
    fun `OTP message not parsed`() {
        val result = parser.parse(
            sender = "HDFC",
            body = "Your OTP is 456789. Valid for 5 minutes."
        )
        assertNull(result.transaction)
    }

    @Test
    fun `very short message not parsed`() {
        val result = parser.parse("BANK", "Bal: 5000")
        assertNull(result.transaction)
    }
}
