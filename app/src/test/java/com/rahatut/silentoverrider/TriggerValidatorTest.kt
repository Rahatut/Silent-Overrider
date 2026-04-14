package com.rahatut.silentoverrider

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.rahatut.silentoverrider.storage.WhitelistStore
import com.rahatut.silentoverrider.validation.TriggerValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TriggerValidatorTest {

    private lateinit var whitelistStore: WhitelistStore

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        whitelistStore = WhitelistStore(context)
        context.getSharedPreferences("sors_whitelist_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        whitelistStore.add("+88 01712345678")
    }

    @Test
    fun validatesAuthorizedSenderAndKeyword() {
        val validator = TriggerValidator(whitelistStore, "OVERRIDE_RING")
        assertTrue(validator.isValid("01712345678", " override_ring "))
    }

    @Test
    fun rejectsUnauthorizedSender() {
        val validator = TriggerValidator(whitelistStore, "OVERRIDE_RING")
        assertFalse(validator.isValid("01999999999", "OVERRIDE_RING"))
    }
}
