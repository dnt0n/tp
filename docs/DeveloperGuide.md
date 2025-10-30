---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Treasura Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

* [**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `archive 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("archive 1")` API call as an example.

<puml src="diagrams/ArchiveCommandSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `archive 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `archiveCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `archiveCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `archiveCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to archive a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `archiveCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `archive 5` command to archive the 5th person in the address book. The `archive` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `archive 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `archive`, just save the person being archived).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Must be a NUS CCA Treasurer
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps
* is in charge of managing multiple member expenses
* is in charge of handling CCA expenses

**Value proposition**: Provides treasurers with a fast, command-driven way to track members, attendance, and payments without heavy accounting tools.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …        | I want to …                              | So that I can…                                   |
|----------|---------------|------------------------------------------|--------------------------------------------------|
| `* * *`  | CCA Treasurer | add new member details                   | build my membership list                         |
| `* * *`  | CCA Treasurer | view member details                      | keep track of members                            |
| `* * *`  | CCA Treasurer | search for members by name or tag        | find records quickly                             |
| `* * *`  | CCA Treasurer | archive inactive members                 | keep my records clean and uncluttered            |
| `* * *`  | CCA Treasurer | record payments from members             | know who has paid fees                           |
| `* * *`  | CCA Treasurer | see the time and date of payments        | track payments chronologically                   |
| `* * *`  | CCA Treasurer | add expenses for CCA purchases           | streamline bookkeeping and avoid manual tracking |
| `* * *`  | CCA Treasurer | archive expenses for CCA purchases       | archive unwanted data                            |
| `* * *`  | CCA Treasurer | sync data automatically when back online | avoid manual backups                             |
| `* * *`  | CCA Treasurer | archive payment from a member            | archive unintended payment                       |


*{More to be added}*

## Use cases

(For all use cases below, the **System** is the `Treasura` and the **Actor** is the `user`, unless specified otherwise)


### Member Management Use Cases

---

**Use case: Add a member**

**MSS**

1. User requests to add a member by specifying details (name, matric number, phone, email, etc.).
2. Treasura validates all input fields.
3. Treasura adds the member to the active list.
4. Treasura confirms the addition with a success message.

   Use case ends.

**Extensions**

* 2a. One or more required fields are missing or invalid (e.g., matric format incorrect, duplicate ID).  
  Treasura shows error: *Invalid command format!
  add: Adds a person to the address book. Parameters: n/NAME p/PHONE e/EMAIL m/MATRICULATION NUMBER [t/TAG]...
  Example: add n/John Doe p/98765432 e/johnd@example.com m/A1234567X t/friends t/owesMoney*.  
  Use case ends.

---

**Use case: Edit a member**

**MSS**

1. User requests to edit details of a specific member using their index.
2. Treasura validates the input.
3. Treasura updates the member record.
4. Treasura displays confirmation of the update.

   Use case ends.

**Extensions**

* 2a. Index is invalid or out of range.  
  Treasura shows error: *The person index provided is invalid*.  
  Use case ends.

* 2b. No prefix is provided (field to edit is unspecified)
  Treasura shows error: *At least one field to edit must be provided*.

---

**Use case: Archive a member**

**MSS**

1. User requests to list members.
2. Treasura shows a list of members.
3. User requests to archive a specific member.
4. Treasura archives the member.

   Use case ends.

**Extensions**

* 2a. The list is empty.  
  Use case ends.

* 3a. The specified index is invalid (non-integer or out of range).  
  Treasura shows error: *The person index(es) provided is invalid*
  Use case ends.

* 4a. The specified member is already archived.  
  Treasura shows error: *Member is already archived*.  
  Use case ends.

* 4b. Storage failure occurs.  
  Treasura shows error: *Unable to save changes*.  
  Use case ends.

---

**Use case: Unarchive a member**

**MSS**

1. User requests to list archived members.
2. Treasura shows a list of archived members.
3. User requests to unarchive a specific member.
4. Treasura unarchives the member and moves them back to the active list.

   Use case ends.

**Extensions**

* 2a. The archived list is empty.  
  Use case ends.

* 3a. The specified index is invalid (non-integer or out of range).
  Treasura shows error: *The person index(es) provided is invalid*.
  Use case ends.

* 4a. The specified member is already active (not archived).  
  Treasura shows error: *One or more selected persons are not archived: [NAME(S)]*.  
  Use case ends.

---

**Use case: Find members**

**MSS**

1. User enters find command with a keyword or list of keywords.
2. Treasura searches for members whose names or tags match the keyword(s).
3. Treasura displays the list of matching members.

   Use case ends.

**Extensions**

* 2a. No member matches the keyword(s).  
  Treasura shows message: *0 persons listed!*.  
  Use case ends.

### Payment Management Use Cases

---

**Use case: Add a payment**

**MSS**

1. User requests to add a payment for one or more members.
2. Treasura validates the indices, amount, and date.
3. Treasura records the payment(s) under each member.
4. Treasura displays success message and updated total.

   Use case ends.

**Extensions**

* 2a. Any index is invalid.  
  Treasura shows error: *The person index(es) provided is invalid*.  
  Use case ends.

* 2b. Date or amount format is invalid.  
  Treasura shows error: *Invalid date. Please use the strict format YYYY-MM-DD (e.g., 2025-01-01) and ensure it is not in the future*.  
  Use case ends.

---

**Use case: Edit a payment**

**MSS**

1. User requests to view payments for a member.
2. Treasura displays the member’s payment list.
3. User requests to edit a specific payment by index.
4. Treasura updates the payment with new details.
5. Treasura confirms successful update.

   Use case ends.

**Extensions**

* 1a. Member index is invalid.  
  Treasura shows error: *The person index(es) provided is invalid*.  
  Use case ends.

* 3a. Payment index does not exist.  
  Treasura shows error: *Payment index is invalid for this person*.  
  Use case ends.

* 4a. New date is invalid.  
  Treasura shows error: *Invalid date. Please use the strict format YYYY-MM-DD (e.g., 2025-01-01) and ensure it is not in the future.*.  
  Use case ends.

---

**Use case: Delete a payment**

**MSS**

1. User requests to delete a payment using `deletepayment MEMBER_INDEX p/PAYMENT_INDEX`.
2. Treasura validates the indices.
3. Treasura deletes the payment record.
4. Treasura shows confirmation message.

   Use case ends.

**Extensions**

* 2a. Invalid member index.  
  Treasura shows error: *Invalid index specified*.  
  Use case ends.

* 2b. Invalid payment index.  
  Treasura shows error: *Invalid payment index #[INDEX] for person: [NAME]*.  
  Use case ends.

---

**Use case: View payments for a member**

**MSS**

1. User requests to view payments for a specific member.
2. Treasura retrieves all payments tied to that member.
3. Treasura displays the list of payments.

   Use case ends.

**Extensions**

* 1a. Invalid member index.  
  Treasura shows error: *The person index(es) provided is invalid*.  
  Use case ends.

* 2a. Member has no payment records.  
  Treasura shows message: *[NAME] has no payments recorded*.  
  Use case ends.

---

**Use case: View all payments**

**MSS**

1. User requests to view all payments using `viewpayment all`.
2. Treasura aggregates all payments across members.
3. Treasura displays total per member and overall cumulative total.

   Use case ends.

**Extensions**

* 2a. No payments exist.  
  Treasura shows an empty list.  
  Use case ends.

---

**Use case: Find payments**

**MSS**

1. User requests to find payments for a member using filters (amount/date/remark).
2. Treasura filters the payment list based on given criteria.
3. Treasura displays the matching payments.

   Use case ends.

**Extensions**

* 1a. Invalid member index.  
  Treasura shows error: *The person index(es) provided is invalid*.  
  Use case ends.

* 2a. No payments match the filters.  
  Treasura shows message: *No payments found for Marcus Lee matching [amount | date | remark]*.  
  Use case ends.



## General System Use Cases

---

**Use case: Undo last action**

**MSS**

1. User requests to undo the most recent reversible command.
2. Treasura reverts the most recent state change.
3. Treasura shows a confirmation of the undone action.

   Use case ends.

**Extensions**

* 1a. There is no action to undo.  
  Treasura shows error: *Nothing to undo*.  
  Use case ends.

* 2a. The last command is not undoable (e.g., non-state-changing action).  
  Treasura shows error: *Last action cannot be undone*.  
  Use case ends.

---

**Use case: Redo a previously undone action**

**MSS**

1. User requests to redo the most recently undone command.
2. Treasura restores the previously undone state.
3. Treasura displays confirmation.

   Use case ends.

**Extensions**

* 1a. No command available to redo.  
  Treasura shows error: *Nothing to redo*.  
  Use case ends.

---

<!-- @@author Roshan1572 -->


**Use case: Exit the application**

**MSS**

1. User enters the `exit` command.
2. Treasura saves all current data to disk.
3. Treasura closes the application.

   Use case ends.


  
### Non-Functional Requirements

### 1. Data Requirements
* **Data Volatility** — Person and payment data should be stored persistently and remain intact between sessions.  
  Data changes (add, edit, archive, payment updates) are only committed upon successful command execution.
* **Data Consistency** — The system must prevent conflicting updates (e.g., deleting a payment after it was archived).  
  Undo/redo operations must preserve logical consistency across all entities.
* **Data Integrity** — Each person must have a unique combination of `Name` and `Matriculation Number`.  
  Archived records must retain their associated payments for traceability.
* **Data Security** — User data is stored locally in JSON format. The application does not transmit any data externally.
* **Data Recoverability** — In the event of an abnormal termination, the most recent successful state should be recoverable upon restart.

---

### 2. Technical Requirements
* **Platform Compatibility** — The application must run on any mainstream OS (Windows, macOS, Linux, Unix) with **Java 17** or above installed.
* **Build System** — The project uses **Gradle** for compilation, testing, and packaging (shadow JAR for distribution).
* **Architecture** — Follows the **AB3 MVC architecture** (`Logic`, `Model`, `Storage`, `UI`) for maintainability and modularity.
* **Logging** — The application should log command execution and errors through `LogsCenter` for debugging and traceability.
* **Error Handling** — Parsing and validation errors should never crash the application; they must show user-friendly error messages.
* **Extensibility** — New commands (e.g., `export`, `import`) should be addable without major refactoring due to consistent parser–command structure.

---

### 3. Performance Requirements
* **Startup Time** — The application should launch and display the active list within **≤ 2 seconds** on a typical laptop.
* **Command Latency** — Each command (`archive`, `unarchive`, `find`, `addpayment`, `deletepayment`, etc.) must execute within **≤ 150 ms** for a dataset of  
  up to **5,000 persons** and **20 payments per person**.
* **Undo/Redo Depth** — The undo/redo system must support **at least 20 reversible steps** without performance degradation.
* **Responsiveness** — UI updates (switching between active/archived views) should occur instantly upon command completion.
* **Storage Efficiency** — The application should remain performant and responsive even with file sizes up to **10 MB**.

---

### 4. Scalability Requirements
* **Data Volume** — The system must handle at least **1,000 active persons** and **20,000 total payments** with no noticeable slowdown.
* **Feature Scalability** — The architecture should support future extensions such as `export`, `import`, or `statistics` without affecting core logic.
* **Storage Format** — The JSON-based storage can be evolved (e.g., adding new fields) while maintaining backward compatibility through the adapter pattern.
* **Multi-entity Extension** — The system can be extended to support new entity types (e.g., CCA Events, Expenses) using the existing command framework.

---

### 5. Usability Requirements
* **Command Efficiency** — A user with above-average typing speed should accomplish most tasks faster using text commands than the mouse.
* **Command Feedback** — All commands must provide clear success or error messages in the result display.
* **Error Recovery** — Invalid commands must not corrupt data and should guide the user toward correct syntax via `MESSAGE_USAGE`.
* **Consistency** — Command syntax and usage follow AB3 conventions (e.g., prefixes like `n/`, `e/`, `m/`, `p/`).
* **Learnability** — First-time users should be able to perform basic actions (add, find, archive, view) within **10 minutes** of exploration.
* **Accessibility** — The UI should be readable and usable on screens as small as **1280×720**, with high-contrast text for visibility.

---

### 6. Constraints
* **Offline Operation** — The application must operate fully offline without network connectivity.
* **Single User Environment** — Only one user instance interacts with the data file at any time (no concurrency control required).
* **No External Database** — All data must be stored locally; the use of external servers or cloud databases is not permitted.
* **File Corruption Handling** — If the data file becomes unreadable, the app should display a clear error message and fall back to an empty dataset.
* **Open Source Requirement** — The full source code must be publicly available on GitHub.
* **Coding Standards** — All code must conform to the project’s Java coding standard and pass Checkstyle verification.

---

### Glossary

* **Mainstream OS** — Commonly used operating systems such as **Windows**, **Linux**, **Unix**, and **macOS**.
* **Student ID** — A unique identification code assigned to each **NUS student** (e.g., A0123456X).
* **CCA** — Stands for *Co-Curricular Activity*; refers to a **student club, society, or organization** in NUS.
* **Archived Person** — A person who has been soft-deleted from the active list but remains in storage for record-keeping.
* **Payment Record** — A transaction entry associated with a person, containing an **amount**, **date**, and optional **remarks**.
* **Predicate** — A filtering condition used in the app’s logic layer to determine which persons are displayed in the UI.
* **Command Word** — The keyword used to trigger a command (e.g., `archive`, `find`, `undo`).
* **Model** — The component responsible for holding data and business logic; updates the UI through observable lists.
* **View** — The user interface layer that reflects the current state of the model (e.g., active list, archived list, payment view).
* **Undo/Redo Stack** — A pair of internal data structures that track the history of changes for reversible commands.

<!-- @@author -->

--------------------------------------------------------------------------------------------------------------------

### **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### **Appendix: Planned Enhancements**
Our team size is 5.
**1. **Payment Dashboard:**  
Add a simple visual dashboard summarizing all payments (e.g., total collected, outstanding, and by category) for better financial overview.

2. **Multi-CCA Support:**  
   Allow users to store and switch between different CCAs’ member and payment data using separate storage files.

3. **Refine Error Messages:**  
   Some error messages are overly generic, returning only the correct command format. Future updates will provide more specific feedback that identifies the exact cause of the error.

4. **Enforce `viewpayment` Precondition:**  
   Currently, `editpayment` and `archivepayment` can be used without viewing a person’s payment list first. This will be fixed by requiring `viewpayment` before editing or deleting payments, ensuring users act within the correct context.

5. **Improve Date Validation Feedback:**  
   The same error message is shown for both invalid date formats and future dates. Future versions will distinguish between the two:
    - *Invalid format:* “Please use YYYY-MM-DD (e.g., 2025-01-01).”
    - *Future date:* “Payment date cannot be in the future.”

6. **Enhance Command Error Handling:**  
   When users enter unknown or misplaced prefixes (e.g., `e/fovfv`), the app currently reports unrelated errors such as “Invalid amount.” This will be updated to show clearer messages like:
   > “Unknown prefix: e/. Please check your command format.”**

7. **Guarded Member Deletion (archive-first flow):**
    Introduce an optional permanent delete pathway that operates only on archived members and requires explicit confirmation (e.g., delete 2 confirm). This clarifies the lifecycle distinction between “hide” (archive) and “purge” (delete), prevents accidental loss, and aligns expectations for users familiar with AB3-style deletion. The User Guide will document the archive-first model, the confirmation step, and the cascade effects (e.g., associated payments removed).

8. **Data Portability & Safe Dataset Reset:**
    Add export/import of full datasets as a single portable snapshot (e.g., JSON/ZIP) and a guarded reset command that wipes only the active dataset after confirmation while auto-backing up the current state. This complements Multi-CCA Support by enabling easy cross-device transfer of a CCA’s records and a clean, auditable way to start fresh for a new cohort without risky manual file operations.

<!-- @@author Roshan1572 -->

### Appendix: Effort

### Overview
The project builds on the AddressBook Level 3 (AB3) foundation but significantly expands its scope and complexity.  
While AB3 manages a single entity type (`Person`), our project introduces **multiple entity states and relationships**:
* **Archived vs Active persons** with distinct filters, views, and persistence logic.
* **Payment records** linked to each person, with support for amount, date, and remarks fields.
* **Undo/Redo** functionality for all mutating commands, increasing both user convenience and implementation complexity.

These extensions required architectural changes across the `Model`, `Logic`, `Storage`, and `UI` layers, while maintaining compatibility with the existing AB3 command architecture.

---

### Challenges Faced
1. **Multi-file updates and merge conflicts**  
   Introducing new attributes (e.g., `archived` flag, payment list) required synchronized updates across the `Person`, `JsonAdaptedPerson`, `Storage`, and `Ui` classes.  
   Coordinating these updates required coordination and communication to minimise merge conflicts and overwrites.

2. **Payment interface design**  
   Designing a flexible payment model that stores multiple payments per person with amount, date, and optional remarks demanded careful consideration of immutability and display ordering.  
   Commands like `addpayment`, `deletepayment`, `editpayment`, and `viewpayment` required custom parsing and validation logic distinct from AB3’s single-field operations.

3. **Archived/Active view management**  
   Implementing `archive`, `unarchive`, and `listarchived` introduced the need for dynamic predicate switching (`PREDICATE_SHOW_ACTIVE_PERSONS` vs `PREDICATE_SHOW_ARCHIVED_PERSONS`).  
   Ensuring that archived persons were excluded from normal search and list results, while still being able to manage their payments required defensive programming and extensive testing across commands.

4. **Undo/Redo functionality**  
   Maintaining consistent application state after consecutive undo/redo operations required snapshot-based history tracking in the `Model`.  
   Edge cases involving sequential operations (e.g., `archive → undo → addpayment → undo → redo`) were challenging to reason about and verify.

5. **UI synchronization**  
   Modifying the UI to display the Archived label and each member's latest payment.

---

### Effort and Achievements
* **Code effort:** approximately **1.5× the effort of base AB3**, due to additional entity relationships, new commands, validation, and UI enhancements.
* **Testing effort:** expanded significantly, as new commands (`archive`, `unarchive`, `undo`, `redo`, and payment operations) required both unit and integration tests to maintain >80% coverage.
* **Collaboration effort:** frequent merges and PR reviews to maintain consistent architecture and coding standards.

**Key achievements:**
* Successfully implemented **two distinct views** for archived and active persons.
* Created a robust **payment interface** that tracks transaction amount, date, and remarks.
* Added **undo/redo** functionality, improving user experience and reliability.
* Enhanced test coverage and logging, ensuring stability under edge cases.

---

### Reuse and Efficiency
A small portion of the project (~10%) reused or adapted existing AB3 utilities and parser logic:
* The `ArgumentTokenizer`, `ParserUtil`, and `CommandResult` classes were reused with minor extensions.
* This reuse allowed us to focus effort on implementing new domain logic (e.g., payment handling, undo/redo, archived filtering) rather than reimplementing core infrastructure.
* The saved effort was redirected toward improving **test depth**, **code readability**, and **UI integration**.

---

### Summary
In summary, our project demonstrates a substantial step beyond AB3 in both **functionality** and **complexity**.  
By integrating **multiple data dimensions**, **state management**, and **user-friendly undo/redo capabilities**, we produced a feature-rich, reliable, and user-oriented application that serves the needs of our target audience.

<!-- @@author -->

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Archiving a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `archive 1`<br>
      Expected: First contact is archived from the list. Details of the archived contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `archive 0`<br>
      Expected: No person is archived. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect archive commands to try: `archive`, `archive x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
