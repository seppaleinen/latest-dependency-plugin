log = new File(basedir, 'build.log')

assert log.exists()
assert log.text.contains('Newer dependency existsDependency {groupId=org.springframework.boot, artifactId=spring-boot-starter-web')

return true