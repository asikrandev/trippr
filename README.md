# trippr
Trippr is an application that uses Acision SDK for communicating via webrtc with a microservice that provides travel recommendations from an elasticsearch node running in Amanzon's Cloud.


The Trippr project has become huge during these weeks. It started as a simple idea and it has grown to become an application with so much potential that it was hard to give some closure so we could present it at the App Challenge. The roadmap for Trippr extends wide and long and we are happy to be able to share the first big milestone here.


The application shows images that the user can swipe/choose swiftly in order for us to create preference and mood vectors according to his reactions. From this simple process we can read a database of destinations and find the one that fits the most and that the user might find more pleasant to travel to. After this we show the cheapest flight from his current location to the main airport of the destination so the user can start his journey right away. All of this process is based in the usage of open content, license free images and open source travel information. 


Future milestones include also looking for hotels, handling more destinations (right now there are about 20), using pictures from social media and its tags, use social profile's information to feed the queries and handling huge amounts of users.

Present limitations mainly focus on the lack of a good scaling architecture and general bugs related to data quality, android versions and other small issues we keep working on.
