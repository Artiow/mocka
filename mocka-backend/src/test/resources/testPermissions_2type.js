const System = Packages.java.lang.System
const LoggerFactory = Packages.org.slf4j.LoggerFactory

const log = LoggerFactory.getLogger(System.class)

log.info('Env:\n{}', System.getenv())
log.info('Properties:\n{}', System.getProperties().toString())

