[README.md](https://github.com/user-attachments/files/24989172/README.md)
# GreenChargePlanner-App


**GreenCharge Planner** is a web application that helps electric vehicle users select the most environmentally friendly time to charge their car by analyzing the UK energy generation mix.

The application fetches real-time and forecasted data from the **Carbon Intensity API** and calculates the optimal charging window based on the highest percentage of clean energy.
 

###  Energy Mix Visualization
- Displays **three pie charts** for:
  - Yesterday
  - Today  
  - Tomorrow  
  - Day after tomorrow  
- Shows the percentage of clean energy for each day.
- Clean energy sources include:
  - Biomass  
  - Nuclear  
  - Hydro  
  - Wind  
  - Solar  
  - Gas
  - Coal
  - Other

###  Optimal Charging Window
- User selects charging duration (**1�6 hours**).
- Backend analyzes **48 hours ahead from the current time**.
- Uses half-hour intervals provided by the API.
- Finds the time window with the **highest average clean energy share**.
- The window may span across two different days.


## Tech Stack

### Frontend
- React + TypeScript  
- Vite  
- Recharts (Pie Charts)  
- Axios  

### Backend
- Spring Boot  
- WebFlux (Reactive Programming)  
- JUnit + Mockito (Tests)  

##  Architecture Overview

### Backend Responsibilities
- Fetch generation data from the Carbon Intensity API.
- Group half-hour intervals by date.
- Calculate the average energy mix for each day.
- Compute clean energy percentage.
- Implement a **sliding window algorithm** to determine the optimal charging period.

### Frontend Responsibilities
- Visualize energy mix data using pie charts.
- Allow the user to input charging duration.
- Display the calculated charging window clearly and intuitively.
