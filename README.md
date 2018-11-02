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

### Debug WELD
Set system prorperty
```-Dorg.jboss.logging.provider=slf4j``` 
in Servers->Tomcat ->General Information->Open Launch Configuration ->Arguments -> VM Arguments

### Enable WELD development mode
Add to web.xml

```
<context-param>
    <param-name>org.jboss.weld.development</param-name>
    <param-value>true</param-value>
</context-param>
```
    

Available at http://localhost:port/<app-path>/weld-probe
