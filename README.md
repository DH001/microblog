Microblog
----------------------------------------------------------

This is a simple microblogging REST service for the ForgeRock technical assessment. 

Although the coding exercise describes a Supervillain blogging site, I have written a generic microblogging site to meet the requirements of blogging and rating that could be deployed with an approriate UI to be used by Supervillains, Superhereos or anyone else.

The application is written in Java 8 with a maven build. The following technologies are used:
- Spring Boot : This gives a quick and reasonably lightweight service framework to quickly deploy and run the application.
- Elasticsearch 5.4.1 : This was selected so that filter, sort and text search could be quickly and simply provided. Although it has drawbacks compared to a relational database (e.g. transactions) I felt that this would not be an issue for simple writes of blog posts and ratings and large search/reads would be more important. In a production environment, backup and security solutions could be implemented.
   
Docker files have been provided to run the application with Elasticsearch. If running standalone you will need an instance of Elasticsearch 5.4.1 running on localhost with ports 9200 and 9300. 


Setup Instructions
------------------

1. To run full build and tests run:  mvn clean install 
2. To just build the docker images without tests run: ./dockerBuild.sh
3. To startup application run:  ./dockerRun.sh
4. Check the application is running:    
> http://localhost:8080/info 

> http://localhost:8080/health 
5. To shutdown application run: ./dockerStop.sh

To Use the REST API
--------------------

1. You can view the REST API docs at :  
>  http://localhost:8080/swagger-ui.html

2. To View All BlogPosts:
> GET   http://localhost:8080/blogposts 

(Initially this will be an empty list)

3. To create a Blog Post: 
> POST  http://localhost:8080/blogposts
{
 "body": "Rob some banks",
 "userId": "joker"
} 

(Note: this will return a 'Location' header in the response with new resource)

4. View all Blog Posts again and your post should appear with a timestamp and id (GUID)
> GET   http://localhost:8080/blogposts 

5. To view this blog post by its ID use this: 
> GET   http://localhost:8080/blogposts/{id}

(where id is the generated id field seem in the previous step)

6. Now update the previous post with some additional text: 
> PUT   http://localhost:8080/blogposts/{id}
{
 "body": "Rob banks and blow up bridges"
}

(the updated post will be returned in repsonse body)

7. Now create a new Blog Post then delete it:
> POST   http://localhost:8080/blogposts
{
 "body": "World peace",
 "userId": "superman"
} 

> DELETE  http://localhost:8080/blogposts/{id_of_previous_post}

(the id will be in the location header returned from the POST)

8. Next, create more blog posts
> POST   http://localhost:8080/blogposts
{
 "body": "Destroy stuff with magnets",
 "userId": "magneto"
} 

> POST   http://localhost:8080/blogposts
{
 "body": "lasers in space",
 "userId": "drdoom"
} 

9. Get a filtered and sorted list
> GET    http://localhost:8080/blogposts?sort=userId:DESC&userIds=magneto&fromDateTime=2016-06-11T15:06:24.627Z&toDateTime=2018-06-11T15:06:24.627Z

(This will get all posts by blofeld in the specified date range, sorted by userid - you can experiement with the values). 

10. You can also 'page' the list if there are lots of posts: 
> GET    http://localhost:8080/blogposts?size=5&offset=0

11. To rate a blog post: 
> POST   http://localhost:8080/blogposts/{id}/ratings    
{
 "rating": 4,
 "userId": "catwomen"
}

12. To see ratings for a blog post:
> GET    http://localhost:8080/blogposts/{id}/ratings

13. Finally, to search for a blog post based on some text:
> GET    http://localhost:8080/search?searchTerm=laser

> GET    http://localhost:8080/search?searchTerm=magnets

(This should return all posts with the word laser in the body text)


Future Functionality
--------------------

Due to limits of time and wanting to get a functional application delivered to ForgeRock, not all the features could be implemented. However, the application was written so it should be easy to extend. 

A. Integration Tests:  There are junit tests but ideally some integration tests (e.g. Rest Easy) that exercise the REST Services running in a docker container would provide simple but useful test coverage.

B. User Login / Management: User authentication and authorization is not implemented yet so any user id can be posted. This would be implemented using Spring Security to provide basic auth so that a token 
would be provided in the request header that Spring would resolve to a User model object. This user object would provide a User ID for creating data. Updates and Delete could be restricted to the user 
who created the blog post and to prevent a user submitting multiple ratings for the same blog post. Using Spring security would allow the configuration of a third-party authentication server. 
Users would be stored in an appropriate data store (not in Elasticsearch). Spring also provides support for protecting against Cross-Origin attacks.

C. Clustered Solution: The REST services are stateless and multiple instances of the Spring Boot application could be deployed behind a load balancer allowing the solution to be scaled as traffic increases. Elasticsearch can also be scaled with increasing traffic (https://www.loggly.com/blog/nine-tips-configuring-elasticsearch-for-high-performance/). 

E. UI Client: There is no UI client at present. Instead I used the REST service via Postman in a browser to test the endpoints.

F. Input sanitisation: In a production environment, sanitisation of data would be implemented using a library (e.g. Antisamy)
 to ensure that malicious content such as Javascript cannot be POSTed or returned from the services. 
