log = new File(basedir, 'build.log')

assert log.exists()
assert log.text.contains('Newer dependency existsDependency {groupId=com.fasterxml.jackson.dataformat, artifactId=jackson-dataformat-xml')

return true