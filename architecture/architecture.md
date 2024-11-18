# Architecture

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

## Communicatie tussen microservices
### Open Feign (Sync)
Om een Post op te halen samen met de reviews en de reacties, wordt er door de Post Microservice bij de GET-request gebruik gemaakt van Open Feign die de Review- en Comment Microservice aanroept. De reviews en reacties (die gelinkt zijn aan de Post aan de hand van de ID's) worden dan meegegeven samen met de Post in de response.

### RabbitMQ (Async)
Er wordt een RabbitMQ bericht naar de queue verzonden wanneer er een nieuwe reactie of review aan een post wordt toegevoegd. De Post Microservice krijgt dit bericht dan binnen en geeft de nodige ID's van de review of reactie mee aan de Post in de database.
