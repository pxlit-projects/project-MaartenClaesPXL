# Architecture

:heavy_check_mark:_(COMMENT) Add a description of the architecture of your application and create a diagram like the one below. Link to the diagram in this document._

![Architecture](https://github.com/pxlit-projects/project-MaartenClaesPXL/blob/main/architecture/Architecture.png)

## Frontend (Angular)
De frontend wordt gemaakt in Angular

## GateWay Service
De GateWay Service is de enige service die direct aangesproken wordt. De Gateway service zorgt ervoor dat alle requests naar de juiste microservice gestuurd wordt.

## Post Service
Deze microservice zorgt ervoor dat redacteurs posts kunnen maken en aanpassen. Deze microservice heeft zijn eigen MySQL database.

## Review Service
Deze microservice zorgt ervoor dat redacteurs posts kunnen goedkeuren of afwijzen. Deze microservice heeft zijn eigen MySQL database.

## Comment Service
Deze microservice zorgt ervoor dat gebruikers kunnen commenten op posts. Deze microservice heeft zijn eigen MySQL database.

## Config Service
Deze microservice zorgt voor de configuraties van de andere microservices.

## Discovery Service
De microservices melden zich aan bij de Discovery Service. Met Open Feign wordt er synchroon gecommuniceerd tussen de microservices

## Messaging Service
De Messaging Service gebruikt RabbitMQ voor de asynchrone communicatie tussen de microservices



[Source](https://docs.microsoft.com/en-us/dotnet/architecture/cloud-native/introduce-eshoponcontainers-reference-app)
