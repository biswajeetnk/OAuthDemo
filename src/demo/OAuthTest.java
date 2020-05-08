package demo;

import static io.restassured.RestAssured.given;
import java.util.ArrayList;
import java.util.List;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import pojo.getCourse;
import pojo.api;
import pojo.courses;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class OAuthTest
{
	public static void main(String[] args) throws InterruptedException
	{
		//since Google has removed automating script 

		String url = "https://rahulshettyacademy.com/getCourse.php?code=4%2FzQEQ7vpF0U_jqcKspzZrr4jpDZ4t_hBNx7b0lku1Tyg_Ic6vWnNKwEJRrZBeYlKR1jqtviG2L2wdsdsORtcsVVg&scope=email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid&authuser=1&prompt=none";
		//parse the below output URL to get/fetch the code
		//Output URL : https://rahulshettyacademy.com/getCourse.php?code=4%2FzQElpuQuOiiOyx9wkH7yGk59-8vA-5P5ndG98heyv6iSkU7ZWkFni4CJahyyLakSnd7ugUVLa8UeViuhTDvlUVU&scope=email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid&authuser=1&prompt=consent 
		String partialCode = url.split("code=")[1]; //splits the URL into 2 parts - [0] = till '?' in above url AND [1] = from code
		String code = partialCode.split("&scope")[0]; //splits the URL into 2 part - [0] = till 'scope' AND [1] = from scope
		System.out.println("The code is : "+code);



		//To get access token :
		String getAccessTokenResponse = 
				given().urlEncodingEnabled(false).log().all().
				queryParams("code", code).
				queryParams("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com").
				queryParams("client_secret", "erZOWM9g3UtwNRj340YYaK_W").
				queryParams("redirect_uri", "https://rahulshettyacademy.com/getCourse.php").
				queryParams("grant_type", "authorization_code")
				.when().log().all().post("https://www.googleapis.com/oauth2/v4/token").asString();
		System.out.println("The getAccessTokenResponse is : "+getAccessTokenResponse);

		JsonPath jp = new JsonPath(getAccessTokenResponse);
		String accessToken = jp.get("access_token");
		System.out.println("The access token is : "+accessToken);


		//To fetch course details from RahuShettyAcademy page :
		getCourse courseDetails = 
				given().log().all().queryParams("access_token", accessToken).expect().defaultParser(Parser.JSON)
				.when().get("https://rahulshettyacademy.com/getCourse.php").as(getCourse.class);

		System.out.println("The istructor name is : "+courseDetails.getInstructor());
		System.out.println("The LinkedIn profile is : "+courseDetails.getLinkedIn());	
		System.out.println("The courses offered by the instructor are : "+courseDetails.getCourses());

		//Get the course names of WebAutomation
		//List<getCourse> courseDetails2 = (List<getCourse>) courseDetails;
		List<api> w = courseDetails.getCourses().getApi();
		for(int j=0;j<w.size();j++)
		{
			System.out.println("API Course Details are : "+w.get(j).getCourseTitle());
		}

	}
}
