default: versioncheck

clean:
	./gradlew clean

build:
	./gradlew build -xtest

jar: clean
	./gradlew jar

versioncheck:
	./gradlew dependencyUpdates

depends:
	./gradlew dependencies

upgrade-wrapper:
	./gradlew wrapper --gradle-version=8.5 --distribution-type=bin