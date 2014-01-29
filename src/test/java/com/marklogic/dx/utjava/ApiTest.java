package com.marklogic.dx.utjava;

import junit.framework.TestCase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;


import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.post;
import static com.jayway.restassured.RestAssured.given;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;




/**
 * Basic API tests that are dependent on the default data set of 1862 email messages, 
 * available from http://developer.marklogic.com/download/code/ut-java/email.zip
 * 
 * Tests are designed to be idempotent and not to break given any early tagging (there is some
 * possibility that making tags with words that are used as search terms in the test could cause issues).
 * 
 * @author ebloch
 *
 */
public class ApiTest extends TestCase {

		@Override
		public void setUp() {
			
			// TODO: bring in system properties for connection details
			
			RestAssured.basePath = "/exercise-1";
		}
		
		@Override
		public void tearDown() {
			
		}
		
		public void testMessage() {
			
			get("/message/foo.xml").then().assertThat().statusCode(404);
			
			ValidatableResponse response = get("/message/mail/a3x5yi4l7iypy7ka.xml").then();
			
			response.assertThat().statusCode(200);
			response.assertThat().contentType(ContentType.JSON);
			
			response.body("subject", equalTo("FYI: MarkMail now indexes users@sling.apache.org"));
			
			response.body("from", equalTo("vidar@idium.no"));

			response.body("date", equalTo("2010-02-16"));
			
			response.body("list", equalTo("org.apache.sling.users"));
			
		}
		
		/**
		 * Pretty basic tests
		 */
		public void testSearch() {
			
			ValidatableResponse response = get("/search?q=&p=").then();
			
			response.assertThat().statusCode(200);
			response.assertThat().contentType(ContentType.JSON);
			
			response.body("totalResults", equalTo(1862));
			
			response.body("results.findall.size", equalTo(10));
			
			response.body("p", equalTo(1));
			
			response = get("/search?q=hello&p=").then();
			
			/* Should really devise a test that hits on each of the specific hit areas in the doc */
			response.body("totalResults", equalTo(9));
			
			response.body("results[0].list", equalTo("org.openlaszlo.laszlo-user"));
			response.body("results[0].date", equalTo("2005-01-20"));
			response.body("results[0].subj", equalTo("[Laszlo-user] Dynamic Production of .lzx documents"));
			response.body("results[0].pers", equalTo("Eric Bloch"));
			response.body("results[0].from", equalTo("bloch@laszlosystems.com"));
			response.body("results[0].uri", equalTo("/mail/zrjwjnpkev2zbzxt.xml"));
			response.body("results[0].snippet", equalTo(			
					"\n      where the compiler would look for a file called hello.jsp and GET it, \nassume the response is text/xml containing LZX code and...\n      ...examples/hello.jsp for you to look at but, again, this...\n    "
			));	
		}
		
		public void testTag() {
			
			Response res = post("/tag?t=");
			ValidatableResponse response = res.then();
			
			response.assertThat().statusCode(200);
			response.assertThat().contentType(ContentType.JSON);
			
			assertEquals(res.asString(), "\"0\"");
			
			String uris = toJSON(new String[] { "/foo/bar.xml" } );
			
			res = given().body(uris).post("/tag?t=hello");
			response = res.then();
			
			response.assertThat().statusCode(400);
			
			/* Tag 2 docs */
			uris = toJSON( new String[] {
					  "/mail/zrjwjnpkev2zbzxt.xml", 
					  "/mail/pfo6x6w7kestl5yg.xml"  
					}) ;
			
			res = given().body(uris).post("/tag?t=hello");
			response = res.then();
			
			response.assertThat().statusCode(200);
			assertEquals(res.asString(), "\"2\"");
			
			response = get("/message/mail/zrjwjnpkev2zbzxt.xml").then();
			response.body("tags", hasItem(equalTo("hello")));
			
			response = get("/message/mail/pfo6x6w7kestl5yg.xml").then();
			response.body("tags", hasItem(equalTo("hello")));
			
			/* One of these docs will fail, so both should fail */
			uris = toJSON( new String[] {
					  "/mail/zrjwjnpkev2zbzxt.xml", 
					  "/mail/bad.xml"  
					}) ;
			
			res = given().body(uris).post("/tag?t=bad");
			response = res.then();
			
			response.assertThat().statusCode(400);
			
			/* Test that the tag did not get set */
			response = get("/message/mail/zrjwjnpkev2zbzxt.xml").then();
			response.body("tags", not(hasItem(equalTo("bad"))));
			response.body("tags", hasItem(equalTo("hello")));


		}
		
		static public String toJSON(Object x) {
			ObjectMapper mapper = new ObjectMapper(); 

			String str;
			try {
				str = mapper.writeValueAsString(x);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				str = "\"Exception!\"";
			}
			return str;
		}
	
}
