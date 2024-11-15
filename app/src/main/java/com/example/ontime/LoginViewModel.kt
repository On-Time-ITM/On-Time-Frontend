import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var phoneNumber by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var phoneNumberError by mutableStateOf<String?>(null)
        private set

    var passwordError by mutableStateOf<String?>(null)
        private set

    fun onPhoneNumberChanged(newValue: String) {
        phoneNumber = newValue
        phoneNumberError = validatePhoneNumber(newValue)
    }

    fun onPasswordChanged(newValue: String) {
        password = newValue
        passwordError = validatePassword(newValue)
    }

    private fun validatePhoneNumber(phone: String): String? {
        return when {
            phone.isEmpty() -> "Phone number is required"
            !phone.matches(Regex("^\\d{10,11}$")) -> "Invalid phone number format"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
    }

    fun onLoginClick() {
        val phoneNumberError = validatePhoneNumber(phoneNumber)
        val passwordError = validatePassword(password)

        if (phoneNumberError == null && passwordError == null) {
            isLoading = true
            // TODO: Implement actual login logic here
            // loginRepository.login(phoneNumber, password)
            isLoading = false
        } else {
            this.phoneNumberError = phoneNumberError
            this.passwordError = passwordError
        }
    }
}