# teamway-shifts

# API docs #
Attached:
- POSTMAN collection
- OpenAPI 3 yaml
- HTML generated based on the yaml file using ```editor.swagger.io```


# Liquibase? (Disclaimer) #
Inside ```src/main/java```, there is a package called
 ```pl.coffeepower.guiceliquibase```. It has been copied from
 [https://github.com/michaljonko/GuiceLiquibase](https://github.com/michaljonko/GuiceLiquibase).
Eventually, this code is not used as I decided to choose Flyway,
but since I tested that code I wanted to spare it as an example
for potential future use, although it is a bad practice to
keep unused and commented code :) (search for ```TODO:liquibase``` phrase).

# Notes on possible further steps
- More tests. Some of them are not implemented. I focused on, in a sense,
end-to-end controller tests. Could think of "more unit" tests.
- Only Local/Dev env is defined with embedded H2 database. Could define
QA/Staging/Production environments.
- Integrate SparkJava with Swagger for automated documentation creation.
So far I found
[https://github.com/manusant/spark-swagger](https://github.com/manusant/spark-swagger)
and
[https://github.com/cesinrm/spark-swagger](https://github.com/cesinrm/spark-swagger)
as a reference.
- Consider more detail responses. For instance POST to create shift returns 201 and empty body. It could be 201 and ShiftDTO with assigned shift ID.