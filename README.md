# influenza

Calculate the most influential people in a social network\*.
The solution is built in `clojure`. It uses `composure` for http routing, `expectations` for the tests and `leiningen` as the task runner.
It reads a social network graph from a file located at `resources/edges` on startup.


\* might also be used to identify the more efficient people to spread a zombie virus.



## Getting started

Before you start, you will need (Leiningen)[http://leiningen.org/] and a version of the Java Development Kit (JDK) installed.

Then, download the project dependencies using:

    $ lein deps


## Interacting with the service

You can start the service with:

    $ lein ring server

Once it's running, it will listen for HTTP requests on port 3000. Then, you can:

- Get the current social influence ranking

    # might take a few seconds
    $ curl -X GET http://localhost:3000/social-influence-ranking | python -m json.tool

- Add a new person and respective connections

    $ curl -X POST http://localhost:3000/persons -H 'Content-Type: application/json' -d '{"id": "person-name", "connections": ["connected-person-1", "connected-person-2"]}'

- Get a person and respective connections

    $ curl -X GET http://localhost:3000/persons/1 | python -m json.tool

- Tag a person as fraudulent

    $ curl -X POST http://localhost:3000/persons/1/fraudulent



## Running the tests

To run the tests:

    $ lein test

To listen and run the tests on every file change:

    $ lein autoexpect
