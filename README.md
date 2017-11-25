# Markov Gibberish

A small microservice that is capable of generation, storage and retrieval of Markov Chain based gibberish.
The intended usage is that the user sends a text and a requested length of the gibberish to the REST API. The user receives back a text response with the generated gibberish.
The user can later send a request to the API to get all the generated content or a particular one.

From the server's perspective the client has three choices:
- to submit (_POST_) a text for gibberishization and storage
- to retrieve (_GET_) all the previous submissions
- to retrieve (_GET_) a particular one


### Gibberish generation
The heart of the application is the `MarkovGibberishGenerator`. The algorithm that is used to generate gibberish is as follows:
- take the incoming text and lower case it
- generate a map of the text where a word points to a list of words
- start from a random position and continue traversing the map until the `length` parameter is reached, accumulating a list words along the way
- concatenate the words in the list into a single space separated string


### Example text
Alice same she shore and seemed to remark myself, as she went back to the heard at Alice hastily, after open her sister. Here, Bill! The Duchess too late it a bit had a sort of they are the Queen. An invitation a little of the ran what it was only down her to the other; the Dodo, a Lory and the please that it must as well very good making a dish of time," she added, "It isn’t a letters".
 
### Project structure
- The `RestServer` class is the starting point for the whole application. It holds implicits that are necessary for the operation of akka streams and reads configuraitons from the `application.conf`
- The `Router` hosts the REST API paths that are exposed to clients
- The model used for persistence and de/serialization is hosted under `Gibberish`
- `Repository` is taking care of the interaction between the REST client and the in-memory db
- `MarkovGibberishGenerator` has been discussed in the previous section
- Tests are located under the `test.scala` package

### Known limitations
It could happen that the algorithm ends up in a state from which there is no way out i.e. there is the word that is selected only appears as a value in the map but not as a key. That leads to an error whenever that word is used as key lookup in the word-to-word map. For simplicity this not is resolved rather simply communicated to the user through a proper response code and error message.
To mitigate this scenario two strategies are to be followed:
- provide richer input texts - with more words and/or with repetitive words
- provide shorter `length` parameter - which unfortunately results in simpler and uninteresting output

### Technologies
- Akka HTTP is the toolkit used for implementing the REST API server
- SkinnyORM is used for relation-mapping along with an H2 in-memory database
- ScalaTest is used for testing purposes

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
                "text": "shore and the other; the please that it a dish of time,\" she added, \"it isn’t a dish of they are the",
                "createdAt": "2017-11-25T16:54:29+01:00"
            },
            {
                "id": 2,
                "text": "lory and the dodo, a bit had a little of the please that it must as well very good making a letters\".",
                "createdAt": "2017-11-25T16:54:32+01:00"
            },
            {
                "id": 3,
                "text": "little of they are the dodo, a lory and seemed to the queen. an invitation a sort of time,\" she went back",
                "createdAt": "2017-11-25T16:54:34+01:00"
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

    ### /gibberish/{gibberish_id}

* Method

    GET

* Request Params

    - gibberish_id - id of the gibberish entry

* Success Response - Json containing the gibberish:
    - Code - 200

    - Payload:
 ```json
    {
        "id": 1,
        "text": "the duchess too late it a dish of the please that it must as she went back to the please that it",
        "createdAt": "2017-11-25T17:12:34+01:00"
    }
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