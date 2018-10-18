# CDI-Weld-RestEasy

## Eclipse
Add the following project facets (Project Properties -> Project Facets:
- Dynamic Web Module
- JAX-RS (REST Web Services)

Define packaging structure for the web application in the Deployment Assembly:
- src/main/java -> WEB-INF/classes
- src/main/resources -> WEB-INF/classes
- src/main/webapp -> /
- Maven Dependencies -> WEB-INF/lib