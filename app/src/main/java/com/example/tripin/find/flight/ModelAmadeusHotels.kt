package com.example.tripin.ui.find

import androidx.annotation.Nullable


object ModelAmadeusHotels {
    data class Result(
        val type: String,
        val hotel: Hotel,
        val available: Boolean,
        val offers: ArrayList<Offer>,
        val self: String)

data class Hotel(
    val type: String,
    val hotelId: String,
    val chainCode: String,
    val dupeId: Int,
    val name: String,
    val rating: Int?,
    val cityCode: String,
    val latitude: Double,
    val longitude: Double,
    val hotelDistance: HotelDistance,
    val address: Address,
    val contact: Contact?,
    val description: Description? = null,
    val amenities: ArrayList<String>?,
    val media: ArrayList<Media?>

    )

data class HotelDistance(
    val distance: Double,
    val distanceUnit: String)

data class Address(
    val lines: ArrayList<String>,
    val postalCode: String?,
    val cityName: String,
    val countryCode: String,
    val stateCode: String? )

data class Contact(
    val phone: String? = null,
    val fax: String? = null,
    val email : String? = null )

data class Media(
    val uri: String? = null,
    val category: String?)

data class Offer(
    val id: String,
    val checkInDate: String?,
    val checkOutDate: String?,
    val roomQuantity: Int?,
    val rateCode: String,
    val category : String,
    val description: Description?,
    val boardType : String,
    val room: Room,
    val guest: Guest,
    val price: Price)

data class Room(
   val type: String,
   val typeEstimated: TypeEstimated,
   val description: Description?)

data class TypeEstimated(
    val category: String,
    val beds: Int,
    val bedType : String)


data class Description(
    val lang: String?,
    val text: String?)

data class Guest(
    val adults: Int)

data class Price(
    val currency: String,
    val base: Double,
    val total: Double,
    val variations: Variation)

data class Variation(
    val average: Average,
    val changes: ArrayList<Change>)

data class Average(
    val base: Double)

data class Change(
    val startDate: String,
    val endDate: String,
    val base: Double)

}


/*
"data": [
{
    "type": "hotel-offers",
    "hotel": {
    "type": "hotel",
    "hotelId": "ONMADATL",
    "chainCode": "ON",
    "dupeId": "700031480",
    "name": "HOTEL ATLANTICO",
    "rating": "4",
    "cityCode": "MAD",
    "latitude": 40.42046,
    "longitude": -3.70456,
    "hotelDistance": {
    "distance": 0.1,
    "distanceUnit": "KM"
},
    "address": {
    "lines": [
    "GRAND VIA 38"
    ],
    "postalCode": "28013",
    "cityName": "MADRID",
    "countryCode": "ES"
},
    "contact": {
    "fax": "34 91 5310210",
    "phone": "34 91 5226480"
},
    "description": {
    "lang": "fr",
    "text": "Le quatre étoiles Hôtel Atlantico, Madrid est célèbre pour sa combinaison de imbattable, endroit facile d'accès central et la qualité des services offerts et d'hébergement. L'hôtel est situé dans un bâtiment historique conçu par le célèbre Don Joaquín Saldaña et est actuellement décorée dans un style néo-classique, mais en même temps, offrant toute la journée moderne des installations technologiques. Hôtel Atlantico propose 105 chambres spacieuses avec air conditionné et WiFi gratuit, navette aéroport gratuit, des salles de réunion, centre d'affaires, un bar / café, une terrasse spacieuse avec vue et bien plus encore. Publique, un parking sécurisé est disponible à environ 50 mètres de l'hôtel."
},
    "amenities": [
    "BUSINESS_CENTER",
    "MEETING_ROOMS",
    "BAR",
    "COFFEE_SHOP",
    "ICE_MACHINES",
    "RESTAURANT",
    "DISABLED_FACILITIES",
    "DISCO",
    "ELEVATOR",
    "EXCHANGE_FAC",
    "FREE_INTERNET",
    "MASSAGE",
    "LOUNGE",
    "AIR_CONDITIONING",
    "NONSMOKING_RMS",
    "ROOM_SERVICE",
    "WI-FI_IN_ROOM",
    "SAFE_DEP_BOX"
    ],
    "media": [
    {
        "uri": "http://uat.multimediarepository.testing.amadeus.com/cmr/retrieve/hotel/EB874AAD4E0C410EB6D3C6841C85522B",
        "category": "EXTERIOR"
    }
    ]
},
    "available": true,
    "offers": [
    {
        "id": "C46373CA4CD42292DC293FD59FE7728D321A98A8C55A4679EE79A311BD5258CF",
        "checkInDate": "2020-04-17",
        "checkOutDate": "2020-04-18",
        "rateCode": "PPP",
        "rateFamilyEstimated": {
        "code": "PRO",
        "type": "P"
    },
        "commission": {
        "percentage": "10"
    },
        "room": {
        "type": "C1S",
        "typeEstimated": {
        "category": "STANDARD_ROOM",
        "beds": 1,
        "bedType": "SINGLE"
    },
        "description": {
        "lang": "EN",
        "text": "Non refundable Prepayment Prepayment, non\nrefundable\nSingle Room, 12 sqm, single bed, free WiFi,"
    }
    },
        "guests": {
        "adults": 1
    },
        "price": {
        "currency": "EUR",
        "total": "171.00",
        "variations": {
        "average": {
        "total": "171.00"
    },
        "changes": [
        {
            "startDate": "2020-04-17",
            "endDate": "2020-04-18",
            "total": "171.00"
        }
        ]
    }
    },
        "policies": {
        "deposit": {
        "acceptedPayments": {
        "methods": [
        "CREDIT_CARD"
        ]
    }
    },
        "paymentType": "deposit"
    }
    }
    ],
    "self": "https://test.api.amadeus.com/v2/shopping/hotel-offers/by-hotel?hotelId=ONMADATL"
},
{
    "type": "hotel-offers",
    "hotel": {
    "type": "hotel",
    "hotelId": "HAMADGEN",
    "chainCode": "HA",
    "dupeId": "700025588",
    "name": "HOTEL REGENTE",
    "rating": "3",
    "cityCode": "MAD",
    "latitude": 40.41951,
    "longitude": -3.70436,
    "hotelDistance": {
    "distance": 0.1,
    "distanceUnit": "KM"
},
    "address": {
    "lines": [
    "MESONERO ROMANOS 9"
    ],
    "postalCode": "28013",
    "cityName": "MADRID",
    "countryCode": "ES"
},
    "contact": {
    "fax": "(+34) 91 5323014",
    "phone": "(+34) 91 5212941"
},
    "description": {
    "lang": "fr",
    "text": "Hôtel situé en plein centre de Madrid, zone Gran Via, entre Callao et la station de métro de Gran Via. Les chambres sont totalement équipées avec climatisation, antenne parabolique, coffre fort, sèche cheveux. L´hôtel dispose de parking (coût 28 euros) et service de laverie."
},
    "amenities": [
    "BAR",
    "COFFEE_SHOP",
    "CAR_RENTAL",
    "ELEVATOR",
    "LAUNDRY_SVC",
    "PARKING",
    "AIR_CONDITIONING",
    "HAIR_DRYER",
    "MOVIE_CHANNELS",
    "PHONE-DIR_DIAL",
    "ROOM_SERVICE",
    "TELEVISION",
    "SAFE_DEP_BOX",
    "MASSAGE",
    "FAX_FAC_INROOM",
    "FREE_INTERNET",
    "NONSMOKING_RMS"
    ],
    "media": [
    {
        "uri": "http://pdt.multimediarepository.testing.amadeus.com/cmr/retrieve/hotel/01C69DCD2DBD4294BEB1EFFD329543D3",
        "category": "EXTERIOR"
    }
    ]
},
    "available": true,
    "offers": [
    {
        "id": "A11CD5DFE9F712706A4821FEA6059DD8772111F4E8B151FE29B29079E8B306E5",
        "checkInDate": "2020-04-17",
        "checkOutDate": "2020-04-18",
        "rateCode": "LPR",
        "commission": {
        "percentage": "10.0"
    },
        "room": {
        "type": "B1S",
        "typeEstimated": {
        "category": "STANDARD_ROOM",
        "beds": 1,
        "bedType": "SINGLE"
    },
        "description": {
        "lang": "EN",
        "text": "BEST AVAILABLE RATE\nSINGLE ONLY ROOM\nRoom Only"
    }
    },
        "guests": {
        "adults": 1
    },
        "price": {
        "currency": "EUR",
        "base": "133.33",
        "total": "133.33",
        "variations": {
        "average": {
        "base": "133.33"
    },
        "changes": [
        {
            "startDate": "2020-04-17",
            "endDate": "2020-04-18",
            "base": "133.33"
        }
        ]
    }
    },
        "policies": {
        "guarantee": {
        "acceptedPayments": {
        "creditCards": [
        "IK",
        "EC",
        "CA",
        "AX",
        "MC",
        "JC",
        "VI"
        ],
        "methods": [
        "CREDIT_CARD"
        ]
    }
    },
        "paymentType": "guarantee",
        "cancellation": {
        "numberOfNights": 1,
        "deadline": "2020-04-16T00:00:00+02:00"
    }
    }
    }
    ],
    "self": "https://test.api.amadeus.com/v2/shopping/hotel-offers/by-hotel?hotelId=HAMADGEN"
}, */
