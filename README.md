# Play Framework Scala Seed with Docker

My seed for Play projects in Scala.  

## Overview
This project is a ready-to-go template that includes:

* Basic configuration defined in application.conf file;
* Docker - create docker image and publish locally by [sbt-native-packager](https://github.com/sbt/sbt-native-packager);
* Logging - basic logging for incoming requests;
* Basic example - sample route displaying application info and configuration;
* API documentation - API documentation created by [swagger](https://github.com/iheartradio/play-swagger)

## Running locally

Clone this repository and launch the app:

```
sbt run
```

then test the ```/info``` endpoint:

```
curl localhost:9000/info
```

and you will get a json response with app 
informations taken from configuration file.

## Running with Docker

For starting the app with Docker first 
create the image with the sbt plugin:

```
sbt docker:publishLocal
```

once you list your docker images, you will see
the gumaz-seed image (that is just 128MB) and you
are ready to run the container:

```
docker run --name MyPlayApp -p 9000:9000 gumaz-seed:0.1.0 -Dplay.http.secret.key=changed
```

The container is running (check ```docker ps```) 
and you can test the ```/info``` endpoint:

```
curl localhost:9000/info
```