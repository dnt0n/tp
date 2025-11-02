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
* depends on some classes in the `Model` component, as it displays `Member` object residing in the `Model`.

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
1. The command can communicate with the `Model` when it is executed (e.g. to archive a member).<br>
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

* stores the Treasura data i.e., all `Member` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Member` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Member>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />



### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both Treasura data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### AddMember feature

The AddMember feature allows Treasura users to add a member into the system.

The sequence diagram below illustrates the interactions within the `Logic` component for adding members.

<puml src="diagrams/AddMemberSequenceDiagram.puml" width="550" alt="Interactions Inside the Logic Component for the `add` Command" />

<box type="info" seamless>

**Note:** The lifeline for `AddMemberCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of the diagram.

</box>

How the `add` command works:
1. When the user enters an `add` command, `LogicManager` passes it to `AddressBookParser`.
2. `AddressBookParser` creates an `AddMemberCommandParser` to parse the command arguments.
3. `AddMemberCommandParser` validates and parses arguments.
4. An `add` object is created and executed.
5. Before execution, the current state is committed for undo/redo functionality.
6. `add` checks if the current MATRICNUM already exists for the specified student(s).
7. If no duplicates are found, the member is added to the system.
8. The updated Treasura is saved to storage.

### Archive feature

The archive feature allows Treasura users to **soft-delete (archive)** a member in the system so they no longer appear in the active list while preserving their data.

The sequence diagram below illustrates the interactions within the `Logic` component for archiving members.

<puml src="diagrams/ArchiveCommandSequenceDiagram.puml" width="550" alt="Interactions Inside the Logic Component for the `archive` Command" />

<box type="info" seamless>

**Note:** The lifeline for `ArchiveCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of the diagram.

</box>

How the `archive` command works:
1. When the user enters an `archive` command, `LogicManager` passes the user input to `AddressBookParser`.
2. `AddressBookParser` creates an `ArchiveCommandParser` to parse the command arguments.
3. `ArchiveCommandParser` validates and parses arguments (e.g., the member index).
4. An `ArchiveCommand` object is constructed and returned to `LogicManager`.
5. Before execution, the current state of the model is **committed** to support Undo/Redo.
6. `ArchiveCommand` retrieves the target member and checks that the member **exists** and is **not already archived**.
7. If validation passes, the member is **marked as archived** (soft-deleted) and the filtered list is updated accordingly.
8. The updated Treasura is **saved to storage**, and a success message is returned to the user.

### AddPayment feature

The add payment feature allows Treasura users to **record a new payment** for one or more members in a single command.

The sequence diagram below illustrates the interactions within the `Logic` component for adding payments.

<puml src="diagrams/AddPaymentSequenceDiagram.puml" width="550" alt="Interactions Inside the Logic Component for the `archive` Command" />

<box type="info" seamless>

**Note:** The diagram uses a generic `Parser` participant to represent the parser layer (e.g., `AddressBookParser` delegating to `AddPaymentCommandParser`). Depending on the concrete implementation, the parser instance’s lifeline may conceptually end at a destroy marker (X), but PlantUML may render it as continuing to the end of the diagram.

</box>

How the `addpayment` command works:
1. The user enters an `addpayment` command. `LogicManager` forwards the raw input to the top-level `Parser`.
2. The `Parser` identifies the command word and delegates to `AddPaymentCommandParser` (conceptually), which:
    - Parses the **member index list** from the preamble (e.g., `1,2`),
    - Parses and validates **amount** (`a/`), **date** (`d/`), and **remark** (`r/`),
    - Constructs an `AddPaymentCommand` encapsulating the parsed arguments.
3. `Parser` returns the `AddPaymentCommand` to `LogicManager`.
4. Before mutating model state, the current model snapshot is **committed** to support **Undo/Redo**.
5. `AddPaymentCommand#execute(model)`:
    - Retrieves the current `displayedList` via `model.getFilteredPersonList()`.
    - For **each specified index**:
        - Resolves the **target member** from `displayedList`.
        - Creates a new **Payment** object from the parsed amount/date/remark.
        - Produces an **updated member** with the new payment appended (preserving immutability).
        - Calls `model.setPerson(target, updated)` to persist the change.
6. After processing all indices, the command composes a **success message** summarizing the added payment and affected members.
7. The updated Treasura is **saved to storage**, and the result is returned to the user.

<box type="tip" seamless>

**Validation highlights**
- **Indices:** Must refer to members in the current displayed list; invalid indices cause the command to fail without partial writes.
- **Amount:** Must be a non-negative monetary value with up to two decimal places.
- **Date:** Must follow the accepted format (e.g., `YYYY-MM-DD`) and be a valid calendar date.
- **Remark:** Free text; excessively long remarks may be truncated or rejected depending on constraints.

</box>

### ViewPayment feature

The view payment feature allows Treasura users to **display all payments** associated with a specific member.

The sequence diagram below illustrates the interactions within the `Logic` component for viewing payments.

<puml src="diagrams/ViewPaymentSequenceDiagram.puml" width="650" alt="Interactions inside the Logic component for the `viewpayment` command" />

<box type="info" seamless>

**Note:** The diagram models the UI initiating parsing and rendering the results. `ViewPaymentCommand` is **non-mutating** and does not affect the Undo/Redo stack.

</box>

How the `viewpayment` command works:
1. The user enters a `viewpayment` command in the UI (e.g., `viewpayment 1`), and the UI forwards the input to `LogicManager`.
2. `LogicManager` delegates to `ViewPaymentCommandParser` to parse the argument (the target member index).
3. The parser validates the index and constructs a `ViewPaymentCommand`.
4. `ViewPaymentCommand#execute(model)` retrieves the current list of members via `model.getFilteredPersonList()`.
5. The target `Member` is resolved from the displayed list, and the member’s `getPayments()` is invoked to fetch their payments.
6. A `CommandResult` containing a **summary string** (e.g., a header and/or count) is returned to the UI.
7. The UI **renders the list of payments** for the selected member.

<box type="tip" seamless>

**Validation highlights**
- **Index:** Must refer to a valid entry in the current displayed member list. An invalid index causes the command to fail.
- **Non-mutating:** The command does **not** change the model (no commit, no Undo/Redo impact).
- **Empty payments:** If the member has no payments, the UI indicates that there are **no payments to show**.

</box>

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

---

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

**Priorities:**  
High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …        | I want to …                              | So that I can…                                   |
|----------|---------------|------------------------------------------|--------------------------------------------------|
| `* * *`  | CCA Treasurer | add new member details                   | build my membership list                         |
| `* * *`  | CCA Treasurer | view member details                      | keep track of members                            |
| `* * *`  | CCA Treasurer | search for members by name or tag        | find records quickly                             |
| `* * *`  | CCA Treasurer | archive inactive members                 | keep my records clean and uncluttered            |
| `* * *`  | CCA Treasurer | record payments from members             | know who has paid fees                           |
| `* * *`  | CCA Treasurer | delete payment from a member             | delete unintended payment                        |
| `* * *`  | CCA Treasurer | see the time and date of payments        | track payments chronologically                   |
| `* * *`  | CCA Treasurer | search for payments                      | find payment records                             |
| `* * *`  | CCA Treasurer | sync data automatically when back online | avoid manual backups                             |


## Use cases

(For all use cases below, the **System** is the `Treasura` and the **Actor** is the `user`, unless specified otherwise)


### Member Management Use Cases

---

**Use case: Add a member**

**MSS**

1. User requests to add a member by specifying details (name, matric number, phone, email, etc.).
2. Treasura validates all input fields.
3. Treasura adds the member to the active list.

   Use case ends.

**Extensions**

* 2a. One or more required fields are missing or invalid (e.g., matric format incorrect, duplicate ID).  
  Treasura shows error: *Invalid command format!
  add: Adds a member to the Treasura. Parameters: n/NAME p/PHONE e/EMAIL m/MATRICULATION NUMBER [t/TAG]...
  Example: add n/John Doe p/98765432 e/johnd@example.com m/A1234567X t/friends t/owesMoney*.  
  Use case ends.

---

**Use case: Edit a member**

**MSS**

1. User requests to edit details of a specific member using their index.
2. Treasura validates the input.
3. Treasura updates the member record.

   Use case ends.

**Extensions**

* 2a. Index is invalid or out of range.  
  Treasura shows error: *The member index provided is invalid*.  
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
  Treasura shows error: *The member index(es) provided is invalid*.
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
  Treasura shows error: *The member index(es) provided is invalid*.
  Use case ends.

* 4a. The specified member is already active (not archived).  
  Treasura shows error: *One or more selected members are not archived: [NAME(S)]*.  
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
  Treasura shows message: *0 members listed!*.  
  Use case ends.

---
  
**Use case: View member details**

1. User enters `view INDEX`.
2. Treasura shows full details of the specified member.
   Use case ends.

Extensions:
- 1a. Index invalid → *Invalid member index.* Use case ends.

---

**Use case: List all active members**

1. User enters `list`.
2. Treasura displays all non-archived members.
   Use case ends.

---

**Use case: List archived members**

1. User enters `listarchived`.
2. Treasura displays only archived members.
   Use case ends.
---

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
  Treasura shows error: *The member index(es) provided is invalid*.  
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
  Treasura shows error: *The member index(es) provided is invalid*.  
  Use case ends.

* 3a. Payment index does not exist.  
  Treasura shows error: *Payment index is invalid for this member*.  
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
  Treasura shows error: *Invalid payment index #[INDEX] for member: [NAME]*.  
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
  Treasura shows error: *The member index(es) provided is invalid*.  
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
  Treasura shows error: *The member index(es) provided is invalid*.  
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
  Treasura will undo the last mutating action (e.g., the user performs "addpayment" -> "list". "addpayment" will be undone).

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
* **Data Volatility** — Member and payment data should be stored persistently and remain intact between sessions.  
  Data changes (add, edit, archive, payment updates) are only committed upon successful command execution.
* **Data Consistency** — The system must prevent conflicting updates (e.g., deleting a payment after it was archived).  
  Undo/redo operations must preserve logical consistency across all entities.
* **Data Integrity** — Each member must have a unique combination of `Name` and `Matriculation Number`.  
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
  up to **5,000 members** and **20 payments per member**.
* **Undo/Redo Depth** — The undo/redo system must support **at least 20 reversible steps** without performance degradation.
* **Responsiveness** — UI updates should be reflected on the screen within 200 ms of user interaction
* **Storage Efficiency** — The application should remain performant and responsive even with file sizes up to **10 MB**.
* **Storage Efficiency** — The application should remain performant and responsive even with file sizes up to **10 MB**.

---

### 4. Scalability Requirements
* **Data Volume** — The system must handle at least **1,000 active members** and **20,000 total payments** with no noticeable slowdown.
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
* **Archived member** — A member who has been soft-deleted from the active list but remains in storage for record-keeping.
* **Payment Record** — A transaction entry associated with a member, containing an **amount**, **date**, and optional **remarks**.
* **Predicate** — A filtering condition used in the app’s logic layer to determine which members are displayed in the UI.
* **Command Word** — The keyword used to trigger a command (e.g., `archive`, `find`, `undo`).
* **Model** — The component responsible for holding data and business logic; updates the UI through observable lists.
* **View** — The user interface layer that reflects the current state of the model (e.g., active list, archived list, payment view).
* **Undo/Redo Stack** — A pair of internal data structures that track the history of changes for reversible commands.

<!-- @@author -->

--------------------------------------------------------------------------------------------------------------------

### **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

#### Launching the application
1. Ensure that you have Java 17 or above installed.
2. Download the latest `.jar` file from the **Releases** page (e.g., `Treasura.jar`).
3. Open a terminal in the directory containing the JAR file.
4. Run a few basic commands such as add, addpayment, archive, and unarchive
5. Details for a quick and easy starting workflow catered to manual testing can be found in our User Guide.


<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### **Appendix: Planned Enhancements**
Our team size is 5.

1. **Payment Dashboard:**  
Add a simple visual dashboard summarizing all payments (e.g., total collected, outstanding, and by category) for better financial overview.

2. **Multi-CCA Support:**  
   Allow users to store and switch between different CCAs’ member and payment data using separate storage files.

3. **Refine Error Messages:**  
   Some error messages are overly generic, returning only the correct command format. Future updates will provide more specific feedback that identifies the exact cause of the error.

4. **Enforce `viewpayment` Precondition:**  
   Currently, `editpayment` and `deletepayment` can be used without viewing a member’s payment list first. This will be fixed by requiring `viewpayment` before editing or deleting payments, ensuring users act within the correct context.

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
While AB3 manages a single entity type (`Member`), our project introduces **multiple entity states and relationships**:
* **Archived vs Active members** with distinct filters, views, and persistence logic.
* **Payment records** linked to each Member, with support for amount, date, and remarks fields.
* **Undo/Redo** functionality for all mutating commands, increasing both user convenience and implementation complexity.

These extensions required architectural changes across the `Model`, `Logic`, `Storage`, and `UI` layers, while maintaining compatibility with the existing AB3 command architecture.

---

### Challenges Faced
1. **Multi-file updates and merge conflicts**  
   Introducing new attributes (e.g., `archived` flag, payment list) required synchronized updates across the `Member`, `JsonAdaptedPerson`, `Storage`, and `Ui` classes.  
   Coordinating these updates required coordination and communication to minimise merge conflicts and overwrites.

2. **Payment interface design**  
   Designing a flexible payment model that stores multiple payments per member with amount, date, and optional remarks demanded careful consideration of immutability and display ordering.  
   Commands like `addpayment`, `deletepayment`, `editpayment`, and `viewpayment` required custom parsing and validation logic distinct from AB3’s single-field operations.

3. **Archived/Active view management**  
   Implementing `archive`, `unarchive`, and `listarchived` introduced the need for dynamic predicate switching (`PREDICATE_SHOW_ACTIVE_PERSONS` vs `PREDICATE_SHOW_ARCHIVED_PERSONS`).  
   Ensuring that archived members were excluded from normal search and list results, while still being able to manage their payments required defensive programming and extensive testing across commands.

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
* Successfully implemented **two distinct views** for archived and active members.
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

