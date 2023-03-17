function main(request) {
    return {
        "status": 200,
        "body": JSON.stringify({
            "message": "Hello World!",
            "request": request
        })
    };
}
