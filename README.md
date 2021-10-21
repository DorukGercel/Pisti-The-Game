# CENG453 Spring 2021 Term Project
### Group 7
#### *Authors*:
- Rabia Varol 2237881
- Doruk Gerçel 2310027

#### *Explanation*:
This is a desktop Pişti Game with both server and client sides. The game has three different singleplayer modes with increasing difficulty (third level is bluffing pişti) and a multiplayer level (fourth level). This game is the term project of METU CENG 453 for the Spring 2021 semester.

#### *Documents*:
- [General Documentation](https://github.com/DorukGercel/Pisti-The-Game/blob/master/documents/Pisti_the_Game_General_Documentation.pdf)
- [User Manual](https://github.com/DorukGercel/Pisti-The-Game/blob/master/documents/Pisti_the_Game_User_Manual.pdf)
- [Database Diagram](https://github.com/DorukGercel/Pisti-The-Game/blob/master/documents/Database%20diagram.png)
- [Swagger Documentation](http://localhost:8080/swagger-ui.html#/)
- [Postman Collections](https://github.com/DorukGercel/Pisti-The-Game/tree/master/documents/postman%20collections)

###### Note: In order to start the project the user must edit the application.properties file in the server with the related credentials.
###### Note: Swagger Documentation is accessible after the project is started. 
###### Note: Normally this project's authentication is provided by Spring Security. In order to demonstrate all API features in the Swagger, we permited all requests for Phase 2.
###### Attention: *Authentication* measures of the API are mentioned in the descriptions. After *login* you will recieve a JWT token. Click *Authorize* and then place the token as "*Bearer ${token}*" and then click *Authorize*. The token is saved for all following requests.
###### Attention: In Postman Collection, Interact Game-Card is actually the simulation of the game therefore the input-output pairs may not match in different game instances.
###### Attention: In Postman execution order please mind the notes in the Swagger file.

