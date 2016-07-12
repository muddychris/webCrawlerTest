Chris Atkinson - web crawler test

General 

I followed the DRY (Don't Repeat Yourself) principle and re-cycled an earlier test program I had written to checkout one of our sites.
It already used jsoup but did not need to handle non-xml pages, this was added for the test.

The maven build file is all new.

To keep things simple, I used an in-memory cache to record which pages had already been visited. The site map was written to stdout.
As the specification of the requirement evolves, these could be changed to use a DB ....

Building and running

check out the project from github
change to the top level directory of the project

mvn clean install
java -jar target/uber-webCrawler-1.0.0.jar 

Stylistic considerations

1) use of exceptions to manage content type

I wouldn't normally use exceptions to support alternate processing paths, I prefer to use exceptions for exceptions.
However, given the nature of html .....

2) use of multiple returns from a method

Some schools of programming thought approve of this whereas others do not.
I think that, used properly with appropriate commenting, that they can keep the code clean and tight.

However, I would always bow to the specific client's coding standards


Things to do

I tested the application against a number of educational sites to see if it would run to completion, however these were very large and the run time required compromised the 2 hour allowance.

Failure to complete the crawl

The Open University site contains a set of what must be automatically generated pages with "nested" url of the form -

.../node/node/.../somewhere

Following the link leads to another page -

.../node/node/node/..../somewhere

And so on ....  (I checked this in the browser too)

The program needs some method of detecting this potentially infinite nesting...

Command line arguments

Obviously the program needs to accept the requested url from user input rather than having it hardcoded in the application.