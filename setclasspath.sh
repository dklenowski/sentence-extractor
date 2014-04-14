VERSION=`cat pom.xml |grep \<version | head -1 | sed 's/<version>//' | sed 's/<\/version>//' | sed 's/ //g'`
export CLASSPATH=`cat .maven_classpath`:target/sentence-extractor-${VERSION}.jar
