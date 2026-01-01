default: versioncheck

clean:
	./gradlew clean

build: clean
	./gradlew build -xtest

jar: clean
	./gradlew jar

versioncheck:
	./gradlew dependencyUpdates

depends:
	./gradlew dependencies

upgrade-wrapper:
	./gradlew wrapper --gradle-version=9.2.1 --distribution-type=bin