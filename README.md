<img src="https://camo.githubusercontent.com/f4ec9fa322baf24255eafd37e485c902cd8568f2/687474703a2f2f726573706563746e6574776f726b2e6769746875622e636f6d2f726e2d6d656d6265722d67726170682d736572766963652f696d616765732f6c6f676f2e706e67"><br>

This is the code base of Respect Network maven plugin for various building tasks

### How to build

    mvn clean
    mvn install

### How to use the plugin

To use the plugin with maven, insert the following into your pom file, such as:

    <plugins>
      ...
      <plugin>
        <groupId>rn.maven.plugin</groupId>
        <artifactId>rn-maven-plugin</artifactId>
        <version>0.1-SNAPSHOT</version>
        <executions>
          <execution>
            <id>execution1</id>
            <phase>generate-sources</phase>
            <configuration>
              <args>
                <param>ApiError</param>
                <param>src/main/java/net/respectnetwork/api/proxy/ProxyApiError.java.template</param>
                <param>util/proxy-api.error.txt</param>
                <param>src/main/java/net/respectnetwork/api/proxy/ProxyApiError.java</param>
              </args>
            </configuration>
            <goals>
              <goal>rn-maven-plugin</goal>
            </goals>
          </execution>
          ...
        </executions>
      </plugin>
      ...
    <plugins>

You can place multiple "&lt;execution&gt;" tags if multiple tasks are needed to be executed

Please make sure that the generated code will be removed after issuing the "mvn clean" command.

-EOF- $Id: README.md,v 1.1.1.1 2015/07/27 05:16:15 zhang Exp $
