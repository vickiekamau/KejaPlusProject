package com.kejaplus.application.Support


import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.apache.commons.text.WordUtils
import org.apache.commons.validator.routines.EmailValidator
import java.util.*


class InputValidator {
    private val REQUIRED = "Field is Required!"
    private val INVALID_DATE = "Date NOT Valid"
    private val SHORT_PASSWORD = "Password should be 4 digits"
    private val INVALID_MAIL = "Not a Valid Email"

    /**private val ourInstance: InputValidator = InputValidator()


    fun getInstance(): InputValidator {
    return ourInstance
    }

    val InputValidator = InputValidator().getInstance()*/

    fun validateRequired(text: TextInputEditText): Boolean {
        return if (fieldNotBlank(text)) {
            true
        } else {
            setErrorWatcher(text, text)
            showError(text, REQUIRED)
            false
        }
    }

    fun validateTextRequired(text: AutoCompleteTextView): Boolean {
        return if (textNotBlank(text)) {
            true
        } else {
            setTextErrorWatcher(text, text)
            showError(text, REQUIRED)
            false
        }
    }


    fun validateRequired(layout: TextInputLayout?, text: TextInputEditText): Boolean {
        return if (fieldNotBlank(text)) {
            true
        } else {
            if (layout != null) {
                showError(layout, REQUIRED)
                setErrorWatcher(text, layout)
            } else {
                showError(text, REQUIRED)
                setErrorWatcher(text, text)
            }
            false
        }
    }

    fun validateRequired(layout: TextInputLayout?, text : AutoCompleteTextView): Boolean {
        return if (textNotBlank(text)) {
            true
        } else {
            if (layout != null) {
                showError(layout, REQUIRED)
                setTextErrorWatcher(text, layout)
            } else {
                showError(text, REQUIRED)
                setTextErrorWatcher(text, text)
            }
            false
        }
    }

    fun validateRequired(editText: TextInputEditText, type: String?, errorView: TextView): Boolean {
        return if (fieldNotBlank(editText)) {
            true
        } else {
            showError(errorView, REQUIRED.replace("Field", WordUtils.capitalize(type)))
            setErrorWatcher(editText, errorView)
            false
        }
    }


    fun validatePassword(editText: TextInputEditText, errorView: TextView): Boolean {
        return if (validateRequired(editText, "Password", errorView)) {
            if (Objects.requireNonNull(editText.text).toString().trim { it <= ' ' }.length < 6) {
                showError(errorView, SHORT_PASSWORD)
                setErrorWatcher(editText, errorView)
                false
            } else {
                true
            }
        } else validateRequired(editText, "Password", errorView)
    }

    fun validatePassword(layout: TextInputLayout, text: TextInputEditText): Boolean {
        return if (validateRequired(layout, text)) {
            if (Objects.requireNonNull(text.text).toString().trim { it <= ' ' }.length < 6) {
                layout.error = "Password should not be less than 6 digits"
                text.error = ""
                false
            } else {
                true
            }
        } else validateRequired(layout, text)
    }

    fun validateConfirmPassword(
        layout: TextInputLayout,
        password: TextInputEditText,
        confirmPassword: TextInputEditText
    ): Boolean {
        if (validatePassword(layout, confirmPassword)) {
            if (passwordMatch(password, confirmPassword)) {
                return true
            } else {
                layout.error = "Passwords Don't Match"
                password.error = ""
                confirmPassword.error = ""
            }
        }
        return false
    }

    private fun passwordMatch(password: TextInputEditText, confirmPassword: TextInputEditText): Boolean {
        return Objects.requireNonNull(password.text).toString()
            .trim { it <= ' ' } == Objects.requireNonNull(confirmPassword.text).toString()
            .trim { it <= ' ' }
    }



    private fun fieldNotBlank(text: TextInputEditText): Boolean {
        return text.text != null && !text.text.toString().isEmpty()
    }
    private fun textNotBlank(text: AutoCompleteTextView): Boolean {
        return text.text != null && !text.text.toString().isEmpty()
    }

    fun validateEmailOptional(emailLayout: TextInputLayout, emailInput: TextInputEditText): Boolean {
        if (emailInput.text != null && !emailInput.text.toString().isEmpty()) {
            val email = emailInput.text.toString()
            return if (EmailValidator.getInstance().isValid(email)) {
                true
            } else {
                showError(emailLayout, INVALID_MAIL)
                setErrorWatcher(emailInput, emailLayout)
                false
            }
        }
        return true
    }

    private fun clearError(view: View?) {
        if (view != null) {
            if (view is TextInputLayout) {
                view.error = null
            } else if (view is TextInputEditText) {
                view.error = null
            } else if (view is TextView) {
                view.error = null
                view.setVisibility(View.GONE)
            }
        }
    }

    private fun showError(view: View, error: String) {
        view.requestFocus()
        if (view is TextInputLayout) {
            view.error = error
        } else if (view is TextInputEditText) {
            view.error = error
        } else if (view is TextView) {
            view.setVisibility(View.VISIBLE)
            view.error = ""
            view.text = error
        }
    }

    private fun setErrorWatcher(input: TextInputEditText, error: View?) {
        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                clearError(error)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
    private fun setTextErrorWatcher(text: AutoCompleteTextView,error: View?){
        text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                clearError(error)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }


}