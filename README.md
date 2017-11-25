# Markov Gibberish

A small microservice that is capable of generation, storage and retrieval of Markov Chain based gibberish.
The intended usage is that the user sends a text and a requested length of the gibberish to the REST API. The user receives back a response a text response with the generated gibberish.
The user can later send a request to the API to get all his generated content or a particular one.

From the servers's perspective the client has three choices:
- to submit (_POST_) a text for gibberishization and storage
- to retrieve (_GET_) all his previous submissions or a particular one


### Gibberish generation
The heart of the application is the `MarkovGibberishGenerator`. The algorithm that is used to generate gibberish is as follows:
- take the incoming text and lower case it
- generate a map of the text where a word points to a list of words
- start from a random position and continue traversing the map until the `length` parameter is reached, accumulating a list words along the way
- concatenate the words in the list into a single space separated string


### Example text
Alice same she shore and seemed to remark myself, as she went back to the heard at Alice hastily, after open her sister. Here, Bill! The Duchess too late it a bit had a sort of they are the Queen. An invitation a little of the ran what it was only down her to the other; the Dodo, a Lory and the please that it must as well very good making a dish of time," she added, "It isn’t a letters".
 
### Project structure
The `RestServer` class is the starting point for the whole application. The `Router` hosts the REST API paths that are exposed to clients.
The model used for persistence and de/serialization is hosted under `Gibberish`. `Repository` is taking care of the interaction between the REST client and the in-memory db.
`MarkovGibberishGenerator` has been discussed in the previous section. Tests are located under the `test.scala` package.

### Known limitations
It could happen that the algorithm ends up in a state from which there is no way out. This is communicated to the user through a proper response code and error message.
To mitigate this two strategies are to be followed:
- provide richer texts - with more words and/or with repetitive words
- provide smaller shorter `length` parameter - which unfortunately results in simpler and uninteresting output

### Technologies
Akka HTTP is the toolkit used for implementing the rest api server. SkinnyORM is used for relation-mapping along with an H2 in memory database.
ScalaTest is used for testing purposes.

### REST interface

**Retrieve a list of all gibberish previously generated**

* URL

    ### /gibberishes

* Method

    GET

* Success Response - Json array containing the gibberish entries:
    - Code - 200

    - Payload:
 ```json
    {
        "items": [
            {
                "id": 1,
                "text": "adashd",
                "createdAt": "2017-11-25T11:51:08+01:00"
            }
        ]
    }
 ```
* Failed Response - failure to retrieve gibberish from the db:
    - Code - 500

    - Payload:
```
Internal Server Error
```

**Retrieve a particular gibberish by id**

* URL

    ### /gibberish?id={gibberish_id}

* Method

    GET

* Request Params

    - gibberish_id - id of the gibberish entry

* Success Response - Json containing the gibberish:
    - Code - 200

    - Payload:
 ```json
    [
    	{
          "observed_date_min_as_infaredate": 5246,
          "observed_date_max_as_infaredate": 5246,
          "full_weeks_before_departure": 0,
          "carrier_id": 10580711,
          "searched_cabin_class": "E",
          "booking_site_id": 763,
          "booking_site_type_id": 3,
          "is_trip_one_way": 0,
          "trip_origin_airport_id": 5497051,
          "trip_destination_airport_id": 2866723,
          "trip_min_stay": 7,
          "trip_price_min": 23361.880859375,
          "trip_price_max": 23361.880859375,
          "trip_price_avg": 23361.87890625,
          "aggregation_count": 1,
          "out_flight_departure_date_as_infaredate": 5246,
          "out_flight_departure_time_as_infaretime": 84000,
          "out_flight_time_in_minutes": 1345,
          "out_sector_count": 2,
          "out_flight_sector_1_flight_code_id": 2367827,
          "out_flight_sector_2_flight_code_id": 3066094,
          "home_flight_departure_date_as_infaredate": 5253,
          "home_flight_departure_time_as_infaretime": 42300,
          "home_flight_time_in_minutes": 745,
          "home_sector_count": 2,
          "home_flight_sector_1_flight_code_id": 8097869,
          "home_flight_sector_2_flight_code_id": 5871365,
          "observation_week": 749,
          "uuid": "0ba36dc4-5df0-43f2-b7cf-512acc547011"
        },
        ...
    ]
 ```
* Failed Response - failure to retrieve gibberish from the db:
    - Code - 500

    - Payload:
```
Internal Server Error
```

* Failed Response - failure to find gibberish for given id:
    - Code - 404

    - Payload:
```
The requested resource could not be found but may be available again in the future.
```

**Submit text for gibberishization**

* URL

    ### /gibberish?length={number}

* Method

    POST

* Request Params

    - length - length of the gibberish text

* Success Response - gibberish text:
    - Code - 200

    - Header 
        
        - Location - path of the generated element that can be used for future retrieval 
        
    - Payload:
 ```
too late it a little of time," she shore and the duchess too late it must as well very good making a
 ```
* Failed Response - failure to retrieve gibberish from the db:
    - Code - 500

    - Payload:
```
Couldn't generate gibberish for the given input/size. Try different input/length.
```

* Failed Response - failure to find gibberish for given id:
    - Code - 500

    - Payload:
```
Requested input is too large to store. Please select a smaller input length.
```