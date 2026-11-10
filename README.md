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
