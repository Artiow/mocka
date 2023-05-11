const System = Java.type("java.lang.System")
const LoggerFactory = Java.type("org.slf4j.LoggerFactory")

const log = LoggerFactory.getLogger(System.class)

log.info('Env:\n{}', System.getenv())
log.info('Properties:\n{}', System.getProperties().toString())
