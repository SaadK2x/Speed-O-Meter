package gps.navigation.speedmeter.utils

data class AddressInfo(
    val completeAddress: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val knownName: String
)