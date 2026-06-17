# charities-registration-frontend

This microservice is an electronic version of the ChA1 form. This form (now withdrawn) can be found at: http://www.hmrc.gov.uk/charities/cha1.pdf

Registration will allow users to register their charity online with HMRC which will allow claiming of gift aid.

### Dependencies

| Service                 | Link                                                |
|-------------------------|-----------------------------------------------------|
| address-lookup-frontend | https://github.com/hmrc/address-lookup-frontend.git |
| auth                    | https://github.com/hmrc/auth                        |
| charities               | https://github.com/hmrc/charities.git               |


## Prerequisites

sm2 and service-manager-config installed
Scala 3, sbt and Java (currently 21)

## Running the Application

When not running journey (ui/acceptance) tests:
```bash
sm2 --start CHARITIES_REGISTRATION_ALL
```

This will start the charities registration service and all** prerequisite services to run in stubbed mode.

** Doesn't include postcode lookup (via address-lookup) - instead select to manually enter addresses

For running the journey tests, it was recommended to bypass the address-lookup service as pages may change without warning breaking tests.
For this reason wiremock stubs are set up by the journey on port 6001. To point the address-lookup to this port, instead run:

```bash
sm2 --start CHARITIES_REGISTRATION_ALL --appendArgs '{"CHARITIES_REGISTRATION_FRONTEND": ["-J-Dmicroservice.services.address-lookup-frontend.port=6001"]}'
```

## Running the Application Locally

Follow the instructions above for running the application, then:

```bash
sm2 --stop CHARITIES_REGISTRATION_FRONTEND
sbt run
```

* If planning to run journey tests, the script run_for_journey_tests.sh will set up the service redirecting the address-lookup as discussed above.

## Connecting locally

* In your browser navigate to [http://localhost:9457/register-charity-hmrc/check-eligibility/register-the-charity](http://localhost:9457/register-charity-hmrc/check-eligibility/register-the-charity)
* When prompted to login, select user type as organisation

## Tests

Below command will run unit tests, integration tests, formatting, coverage and check dependencies:

```bash
./run_all_tests.sh
```

## Licence

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
