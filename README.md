# Exercise Instructions

0. Create a MarkLogic database and attach a REST API instance running on port `8009`.
1. Load the [email data](http://developer.marklogic.com/download/code/ut-java/email.zip) into the Database.  Make sure each email message is assigned a URI that starts with `/mail` and matches the filename sources. For example, `/mail/zrjwjnpkev2zbzxt.xml`.
2. Implement the 3 endpoints described in this [spec](#spec).
3. Deploy the endpoints in an HTTP server (e.g., Tomcat or your preference) running on port `8080`. 
4. Run the unit tests in any manner you see fit. Via maven, you can simply do this, 
   by specifying the `baseURI` and `basePath` to your HTTP API, via properties like: 
  
  		% mvn test '-DbaseURI=http://localhost:8080' '-DbasePath=/ut-java' 

5. After the tests succeed, try deploying, configuring, and running the provided [webapp](https://github.com/marklogic/ut-java/tree/master/src/main/webapp) as well.  You can find it in the ut-java repo aside the unit tests.

<a name="spec"></a>
# HTTP APIs

There are 3 APIs to implement:

  - [GET /message{uri}](#message)
  - [GET /search?query=query&p=page](#search)
  - [POST /tag?t=tag](#tag)

<a name="messge"></a>
### GET /message{uri}

#### Summary
    Return a specified email message

#### Request 

    - Method: GET
    - Request line:
        - Query params: None
        - Path after initial "/message" is interpreted as uri of email message.  
        
          For example, if the full path is "/message/mail/1234.xml", the uri for the request
          message is "/mail/1234.xml"

#### Responses

	On Success: 
	
    - Content-type: application/json
    - Status code: 200
    - Message encoded as JSON Object.  Sample response:
        {
            "subject": "Subject of my email is...",
            "list": "org.apache.http-client.",
            "from": "foo@bar.com",
            "date": "2010-02-16",
            "tags": [ "silly" ]
            "body": "My email is blah blah ...."
        }

	On failure:
	
	- Status code: 404
	
<a name="search"></a>
### GET /search?q=query&p=page

#### Summary

    Return search results for the given keyword query.  Return a list of documents
    where the keyword is in the email subject, body, list, set of tags, or from header.

    When the query is empty, the entire corpus should be returned (in pages).

    Results to be returned in pages of 10.

#### Request
    
    - Method: GET
    - Request line:
        - Query params
            - q: keyword(s) for search
            - p: page for results.  results to be returned in pages of 10.  

#### Response

	On Success
	
    - Content-type: application/json
    - Status code: 200
    - Body encoded as JSON Object.  Sample response:
        {
            p: pageNumber,
            results: [
                {
                    "list": "com.acme.foo-discussion",
                    "date": "2012-10-2",
                    "subject": "My email is about this",
                    "pers": "John Doe",
                    "from": "john.doe@company.com",
                    "tags": [ "tag1", "tag2", ..., "tagN" ],
                    "uri": "/mail/843902840938042.xml",
                    "snippet": "hello, my name is John Doe..."
                },
                {
                    "list": "com.acme.bar-discussion",
                    "date": "2012-10-21",
                    "subject": "My email is about that",
                    "pers": "Jane Doe",
                    "from": "jane.doe@company.com",
                    "tags": [ ],
                    "uri": "/mail/12345.xml",
                    "snippet": "hello, my name is Jane Doe..."
                },
                ...
                {
                    ...
                }
            ],
            total: totalResults
        }

<a name="tag"></a>
### POST /tag?t=tag

#### Summary

    Tag a set of specified messages with the given string.  Specifically, add
    an element to the document for the tag, if it does not already exist.
    Tag them all transactionally.  Either all messages are tagged or none.
    
    For example, for a tag of "foo", add `<tag>foo</tag>` as a child of the `<message>` 	element for all provided message URIs.

#### Request

    - Method: POST
    - Request line:
        - Query params
            - t: tag string
        - Body
            Content-type: application/json
            Encoded as a JSON array of Strings, each one a URI 
            Example:

            [
                "/mail/32910312.xml",
                "/mail/3829103829013.xml",
                .
                .
                .
                "/mail/878392323.xml"
            ]


#### Response

	On success
	
    - Content-type: application/json

    - Status code: 200
    - Body encoded as JSON String.  Sample response:

        "10"

	On failure
	
    - Status code: 400 or 500

