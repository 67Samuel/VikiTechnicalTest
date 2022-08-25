# VikiTechnicalTest
The assignment is to create a simple currency converter app, as if it were a real production app I have to maintain and extend.

## User needs brainstorming
### User would want quick and simple conversion
- large buttons/text
- clean interface
- images for countries
- minimize number of taps needed to get results

### User story
>I'm in the US, walking along a road, I know I have about $50 SGD in my pocket and would like to know if it can last me the rest of the day. Therefore, I want to know how much $50 SGD is in USD. I take out my phone and open the app. 
> - I would want the app to present me with a keypad so I can write the number in as soon as possible. This is not a complex task and therefore is almost a part of my flow of thought, so I want to be able to immediately write down what I'm thinking without interruption.
> - I'd like to see that the 'to' and 'from' fields are already filled, but don't really expect it. At the very least, I would want the last state to be filled, or my home country to be the 'from' field.
> - I'd like the country selections to be searchable, fast, and not cause me to regret using the app and just Google it instead.

## Maintainance and Extension
For the purpose of ease of maintainance and extension, the app should be modular and well documented (to keep the code neat and understandable), reduce coupling through dependency injection and coding against interfaces.

## Architecture
Clean Architecture (this is recommended by Android and therefore are more familiar with this) with MVI.

### Packages
business: datasources and domain
di: dependency injection (using Hilt)
presentation: using MVI

### Database
Local database (using Datastore because we only want to store a small amount of data) so that we can get responses even when offline (must inform user that we are using potentially stale data)

## Further Improvements
- Guess the 'To' field using location information
- Store a short history of previous calculations
- Make network requests only if the last request was made more than <time interval> ago
- Reduce hardcoding especially with regards to the country codes
