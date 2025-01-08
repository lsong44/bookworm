# Requirements for the application

This application is a tool facilitating a group of users to read books. Each reader will be able to log their activities in the app each day, which is visible to all other readers within a reading group, and readers who does not check in for two days in a row will face a fine.

There are two types of members for each group: admin and user. Admins can control the list of users: adding, deleting, promoting users to admins, etc. Admins also control the configuration of the group. For example, setting up skipping dates, marking the starting of each day, making group announcements, etc. Users have access to the group only if they are added by the admin. After being added, they can log their reading activities, including the title of the book they are reading and the notes taken from the book or comments to what they've read each day. An admin can add themselves as a user if they want to participate in reading activities. 

If there are two days (or other number of days setting up by the admin) in a row that a user does not log any reading activities, they will be deleted automatically from the group.

Each user can create an arbitrary number of reading groups and they will be automatically grant admin access to that group. 

When creating a group, the owner must specify the max number of group members. If more than that number of group members are added, based on the order of the invitation, the excess ones will be on a waiting list and will be offically added when any existing member was evicted from that group.


Enhanced activities:
- User can request membership to a group
- Auto filling book title
- Auto filling book quotes
- Control observability of reading activities for each reader