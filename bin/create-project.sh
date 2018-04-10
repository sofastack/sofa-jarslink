name=$1

if [ -z "$name" ]; then  
    echo "need name"
    exit 2  
fi

URL="https://private-alipayobjects.alipay.com/alipay-rmsdeploy-image/rmsportal/xJXQkCxicEVnAvkHWXgI.gz"
installer_file="project-template.tar.gz"

function download {
    echo "Downloading Titan project template: $URL"
    curl -# $URL > $installer_file || exit
}

function unzip {
    echo "unzip $installer_file"
    tar -zxvf $installer_file
}

function createProject
{
  mkdir -p $name/src/main
  mkdir -p $name/src/main/java/com/mybank/bkcommon/collector/$name
  mkdir -p $name/src/main/java/com/mybank/bkcommon/collector/$name/collector
  mkdir -p $name/src/main/java/com/mybank/bkcommon/collector/$name/model
  mkdir -p $name/src/main/resources/META-INF/spring
  mkdir -p $name/src/main/resources/view
  
  mkdir -p $name/src/test
  mkdir -p $name/src/test/java/com/mybank/bkcommon/collector/$name/util
  mkdir -p $name/src/test/resources/
  
  cp project-template/helloworld.vm $name/src/main/resources/view/helloworld.vm
  cp project-template/HelloWorld.java $name/src/main/java/com/mybank/bkcommon/collector/$name/model/HelloWorld.java
  cp project-template/HelloWorldCollector.java $name/src/main/java/com/mybank/bkcommon/collector/$name/collector/HelloWorldCollector.java
  cp project-template/pom-template.xml $name/pom.xml
  cp project-template/integration.xml $name/src/main/resources/META-INF/spring/integration.xml
  cp project-template/QuickStarter-template.java  $name/src/test/java/com/mybank/bkcommon/collector/$name/QuickStarter.java
  cp project-template/integration-test.xml $name/src/test/resources/integration-test.xml
  cp project-template/spring-template.xml $name/src/main/resources/META-INF/spring/$name.xml
  cp project-template/log4j-template.xml $name/src/test/resources/log4j.xml
  cp project-template/log4j-main-template.xml $name/src/main/resources/log4j.xml
  
  sed -i '' 's/${package_name}/'$name'/g' $name/src/test/java/com/mybank/bkcommon/collector/$name/QuickStarter.java
  sed -i '' 's/${project_name}/'$name'/g' $name/pom.xml 
  sed -i '' 's/${project_name}/'$name'/g' $name/src/test/resources/integration-test.xml
  sed -i '' 's/${project_name}/'$name'/g' $name/src/main/resources/META-INF/spring/$name.xml
  sed -i '' 's/${package_name}/'$name'/g' $name/src/main/java/com/mybank/bkcommon/collector/$name/model/HelloWorld.java
  sed -i '' 's/${package_name}/'$name'/g' $name/src/main/java/com/mybank/bkcommon/collector/$name/collector/HelloWorldCollector.java
  sed -i '' 's/${project_name}/'$name'/g' $name/src/main/resources/log4j.xml
  echo "Create Titan Module $name Finish"
}

download
unzip
createProject