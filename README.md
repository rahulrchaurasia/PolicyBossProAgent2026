# PolicyBossProAgent2026

. What should we store where?
Data	Recommended place	Example
Login status	DataStore	isLoggedIn
User ID	DataStore	Ss_Id
Server/app version	DataStore	serverVersion
Last API sync time	DataStore	lastSyncTime
Leads list	Room Database	Multiple Lead records
Sales list	Room Database	Multiple Sales records
Dashboard data	Room Database (if persistence is needed)	Earnings, policy counts
Temporary UI state	ViewModel	Selected tab, filters, loading

Important: AppDataManager (DataStore) is not meant to store large arrays of Leads, Sales or other business objects.

Use Room for that.

Why? Room can store structured records, query them, filter them, and expose them as Flow. DataStore is better suited to small preferences and metadata.

1. Recommended approach: API → Room → Multiple modules
Suppose your common API returns:
- Dashboard data
- Leads list
- Sales list
- Policy list
- User information
The flow should be:

               App Startup
 Initial session and data refresh decision
		  ||
		  ||
Initial session and data refresh decision
                  ||
		  ||
    Save response in Room Database
		  ||
		  ||
       Home                   Leads
   Dashboard data           Lead records

      Sales                   Policy
  Sales records             Policy records

		  
Each module observes its own Room data without repeating the common API call.

2. Where should we call the API?
I recommend this separation:
Layer	Responsibility
AppStartupCoordinator	Decides when shared data should be fetched
CommonRepository	Calls the common API
Room Database	Stores the response by module
DashboardRepository	Reads dashboard data
LeadRepository	Reads Lead data
SalesRepository	Reads Sales data
Each ViewModel	Observes its own repository

My final recommendation
For your app, I would choose:

Common API → CommonRepository → Room → Feature Repositories → Feature ViewModels → Compose UI.

>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
>
    How the complete data flow works :---->
   ...................................
   Let's assume your Horizon API returns common application data.
   Step 1 — User logs in
   Login success → AppStartupCoordinator
   Step 2 — Fetch shared data
   CommonRepository → Horizon API
   Step 3 — Save the response
   Map API DTOs → Room entities → DAOs
   Step 4 — Feature repositories observe Room
   HomeRepository
   LeadsRepository
   DashboardRepository
   SalesRepository
   Step 5 — ViewModels expose UI state
   StateFlow → Compose UI
   What happens when the user switches tabs?
   Example:
1. User opens Home.
2. Shared API is fetched and stored in Room.
3. User opens Leads.
4. LeadsRepository reads Leads-related information from Room.
5. User opens Dashboard.
6. DashboardRepository reads Dashboard-related information from Room.
7. User opens Sales.
8. SalesRepository reads Sales-related information from Room.


...................................
     Why is this better?
...................................
Imagine a user directly opens the Sales tab after login.
If Horizon API initialization is tied to HomeViewModel, you could end up with unnecessary dependencies on Home navigation.
With AppStartupCoordinator, shared data initialization is independent of whichever tab the user opens.
Also, do not block navigation unnecessarily while the network is unavailable. Let the features display cached Room data and refresh in the background.

>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
9. How should we handle refresh and duplicate API calls?
   I recommend a cache-first strategy.
   Situation	Recommended behavior
   First login	Fetch Horizon API
   App reopened	Read Room immediately
   Data is fresh	Avoid unnecessary API call
   Data is expired	Fetch updated data
   User pulls to refresh	Trigger refresh
   Network unavailable	Continue showing cached data
   User logs out	Clear user-specific cached records

