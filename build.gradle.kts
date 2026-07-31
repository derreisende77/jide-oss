version = "3.7.15.1-SNAPSHOT"

plugins {
	`java-library`
	`maven-publish`
}

repositories {
	mavenCentral()
}

// check required Java version
if( JavaVersion.current() < JavaVersion.VERSION_17 )
	throw RuntimeException( "Java 17 or later required (running ${System.getProperty( "java.version" )})" )

// log version, Gradle and Java versions
println()
println( "-------------------------------------------------------------------------------" )
println( "JIDE Version: $version" )
println( "Gradle ${gradle.gradleVersion} at ${gradle.gradleHomeDir}" )
println( "Java ${System.getProperty( "java.version" )}" )
println()

java {
	withSourcesJar()
	withJavadocJar()
}

sourceSets {
	main {
		java.setSrcDirs( listOf( "src" ) )
		resources.setSrcDirs( listOf( "src" ) )

		java.include( "**/*.java" )
		resources.exclude( "**/*.java", "**/*.psd" )
	}
	test {
		java.setSrcDirs( listOf( "test" ) )
	}
}

val demoSourceSet = sourceSets.create( "demo" ) {
	java.setSrcDirs( listOf( "demo" ) )
	compileClasspath += sourceSets["main"].output
	runtimeClasspath += sourceSets["main"].output
}

dependencies {
	testImplementation( "com.formdev:flatlaf:3.7.2" )
	testImplementation( "com.formdev:flatlaf-jide-oss:3.7.2" ) {
		exclude( group = "com.formdev", module = "jide-oss" )
	}
	testImplementation( platform( "org.junit:junit-bom:6.1.2" ) )
	testImplementation( "org.junit.jupiter:junit-jupiter" )
	testRuntimeOnly( "org.junit.platform:junit-platform-launcher" )

	add( demoSourceSet.implementationConfigurationName, "com.formdev:flatlaf:3.7.2" )
	add( demoSourceSet.implementationConfigurationName, "com.formdev:flatlaf-jide-oss:3.7.2" ) {
		exclude( group = "com.formdev", module = "jide-oss" )
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set( 17 )
	options.encoding = "UTF-8"
}

tasks.test {
	dependsOn( "flatLafLightTest", "flatLafDarkTest" )
	useJUnitPlatform {
		excludeTags( "flatlaf" )
	}
	systemProperty( "java.awt.headless", "true" )
	jvmArgs( "--enable-native-access=ALL-UNNAMED" )
}

fun Test.configureFlatLafTest( theme: String ) {
	group = "verification"
	description = "Runs Swing component tests under FlatLaf $theme mode."
	testClassesDirs = sourceSets["test"].output.classesDirs
	classpath = sourceSets["test"].runtimeClasspath
	useJUnitPlatform {
		includeTags( "flatlaf" )
	}
	systemProperty( "java.awt.headless", "true" )
	systemProperty( "jide.test.flatlaf.theme", theme )
	jvmArgs( "--enable-native-access=ALL-UNNAMED" )
}

tasks.register<Test>( "flatLafLightTest" ) {
	configureFlatLafTest( "light" )
}

tasks.register<Test>( "flatLafDarkTest" ) {
	configureFlatLafTest( "dark" )
}

tasks.register<JavaExec>( "runDemo" ) {
	group = "application"
	description = "Runs the interactive FlatLaf JIDE control showcase."
	classpath = demoSourceSet.runtimeClasspath
	mainClass.set( "com.github.derreisende77.FlatLafControlShowcase" )
	jvmArgs( "--enable-native-access=ALL-UNNAMED" )
}

tasks.register<JavaExec>( "smokeDemo" ) {
	group = "verification"
	description = "Constructs the FlatLaf JIDE control showcase in headless mode."
	classpath = demoSourceSet.runtimeClasspath
	mainClass.set( "com.github.derreisende77.FlatLafControlShowcase" )
	args( "--smoke" )
	systemProperty( "java.awt.headless", "true" )
	jvmArgs( "--enable-native-access=ALL-UNNAMED" )
}

tasks.register<JavaExec>( "renderDemoPreview" ) {
	group = "verification"
	description = "Renders light and dark screenshots of the control showcase."
	classpath = demoSourceSet.runtimeClasspath
	mainClass.set( "com.github.derreisende77.FlatLafControlShowcase" )
	args( "--render" )
	jvmArgs( "--enable-native-access=ALL-UNNAMED" )
}

tasks.jar {
	manifest.attributes(
		"Implementation-Version" to project.version
	)

	exclude( "apple/**", "com/apple/**" )
}

tasks.named<Jar>( "sourcesJar" ) {
	exclude( "apple/**", "com/apple/**" )
}

tasks.javadoc {
	options {
		this as StandardJavadocDocletOptions
		encoding = "UTF-8"
		charSet = "UTF-8"
		docEncoding = "UTF-8"
		use( true )
		addStringOption( "Xdoclint:none", "-Xdoclint:none" )
	}
}


publishing {
	publications {
		create<MavenPublication>( "maven" ) {
			artifactId = "jide-oss"
			groupId = "com.formdev"

			from( components["java"] )

			pom {
				name.set( "JIDE Common Layer" )
				description.set( "JIDE Common Layer (Professional Swing Components)" )
				url.set( "https://github.com/JFormDesigner/jide-oss" )

				licenses {
					license {
						name.set( "GPL with classpath exception" )
						url.set( "http://www.gnu.org/licenses/gpl.txt" )
					}
					license {
						name.set( "Free commercial license" )
						url.set( "http://www.jidesoft.com/purchase/SLA.htm" )
					}
				}

				developers {
					developer {
						id.set( "jidesoft" )
						name.set( "jidesoft" )
						email.set( "support@jidesoft.com" )
					}
				}

				scm {
					connection.set( "scm:git:git://github.com/JFormDesigner/jide-oss.git" )
					url.set( "https://github.com/JFormDesigner/jide-oss" )
				}
			}
		}
	}
}
