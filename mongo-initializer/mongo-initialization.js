function sleep(millis) {
  return new Promise(resolve => setTimeout(resolve, millis));
}

function authIsRequired() {
  try {
    // try to get access to rs
    rsIsInitiated();
    return false;
  } catch (e) {
    if (e.code !== 13) throw e;
    return true;
  }
}

function rsIsInitiated() {
  try {
    rs.status().ok;
    return true;
  } catch (e) {
    if (e.code !== 94) throw e;
    return false;
  }
}

async function init() {
  if (!authIsRequired()) {
    if (!rsIsInitiated()) {
      // initiate rs if it's not
      const rsId = process.env.MONGO_RS_ID;
      const rsHost = process.env.MONGO_RS_HOST;
      rs.initiate({_id: rsId, members: [{_id: 0, host: rsHost}]});
      // wait for initialization
      // noinspection StatementWithEmptyBodyJS
      while (!rsIsInitiated());
      await sleep(1000);
    }

    db.createUser({
      user: process.env.MONGO_ROOT_USERNAME,
      pwd: process.env.MONGO_ROOT_PASSWORD,
      roles: [{role: 'root', db: 'admin'}]
    });
  }
}

init();
