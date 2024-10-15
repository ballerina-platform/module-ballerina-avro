// Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

public type Student record {
    string name;
    string subject;
};

public type Student1 record {
    string name;
    byte[] favorite_color;
};

type Students record {
    string name;
    float age;
};

type StudentRec record {
    string name;
    boolean under19;
};

public type Person record {
    string name;
    int age;
};

public type Course record {
    string? name;
    int? credits;
};

public type Instructor record {
    string? name;
    Student? & readonly student;
};

public type Lecturer record {
    string? name;
    Instructor instructor;
};

public type Lecturer1 readonly & record {
    string? name;
    Instructor & readonly instructor;
};

public type Lecturer2 record {
    string? name;
    int age;
    Instructor & readonly instructor;
};

public type Lecturer3 readonly & record {
    map<int> & readonly name;
    int age;
    Instructor & readonly instructor;
};

public type Lecturer4 readonly & record {
    map<int> & readonly name;
    byte[] byteData;
    ByteRecord? instructor;
};

public type Lecturer5 record {
    map<int>? & readonly name;
    byte[]? bytes;
    Instructor? & readonly instructorClone;
    Instructor? instructors;
};

public type Lecturer6 record {
    boolean? temporary;
    map<int>? & readonly maps;
    byte[]? bytes;
    int? age;
    string? name;
    Numbers? number;
    float? floatNumber;
    string[]? colors;
    Instructor? & readonly instructorClone;
    Instructor? instructors;
};

public type ByteRecord readonly & record {
    byte[] byteData;
};

type UnionRecord record {
    string? name;
    int? credits;
    float value;
    StudentRecord? student;
};

type MultipleUnionRecord record {
    string? name;
    int? credits;
    float value;
    string|StudentRecord? student;
};

type StudentRecord record {
    string? name;
    string? subject;
};

public type Color record {
    string? name;
    string[] colors;
};

type Color1 record {
    string name;
    byte[] colors;
};

public type FixedRec record {
    byte[] fixed_field;
    string other_field;
};

enum Numbers {
    ONE,
    TWO,
    THREE,
    FOUR
};

public type SchemaChangeKey record {
    string databaseName;
};

public type Value record {
    int ID;
    string? OfferID;
    int? PropertyId;
    string? PlayerUnityID;
    string? HALoOfferStatus;
    int? StatusDateTime;
    int? OfferSegmentID;
    int? RedemptionDate;
    int? OfferItemID;
    string? OfferPrizeCode;
    float? AmountRedeemed;
    int? ItemQuantity;
    string? OfferType;
    int? CreatedDate;
    string? CreatedBy;
    int? UpdatedDate;
    string? UpdatedBy;
};

public type Source record {
    string version;
    string connector;
    string name;
    int ts_ms;
    string snapshot;
    string db;
    string? sequence;
    string schema;
    string 'table;
    string? change_lsn;
    string? commit_lsn;
    int? event_serial_no;
};

public type Block record {
    string id;
    int total_order;
    int data_collection_order;
};

public type Envelope record {
    Value? before;
    Value? after;
    Source 'source;
    string op;
    int? ts_ms;
    Block? 'transaction;
};

public type Value2 record {
    int CorePlayerID;
    string? AccountNumber;
    string? LastName;
    string? FirstName;
    string? MiddleName;
    string? Gender;
    string? Language;
    boolean? Discreet;
    boolean? Deceased;
    boolean? IsBanned;
    string? EmailAddress;
    boolean? IsVerified;
    string? EmailStatus;
    string? MobilePhone;
    string? HomePhone;
    string? HomeStreetAddress;
    string? HomeCity;
    string? HomeState;
    string? HomePostalCode;
    string? HomeCountry;
    string? AltStreetAddress;
    string? AltCity;
    string? AltState;
    string? AltCountry;
    int? DateOfBirth;
    int? EnrollDate;
    string? PredomPropertyId;
    string? AccountType;
    int? InsertDtm;
    string? AltPostalCode;
    int? BatchID;
    string? GlobalRank;
    float? GlobalValuationScore;
    string? PlayerType;
    string? AccountStatus;
    string? RegistrationSource;
    string? BannedReason;
    string? TierCode;
    string? TierName;
    int? TierEndDate;
    boolean? VIPFlag;
};

public type Source2 record {
    string version;
    string connector;
    string name;
    int ts_ms;
    string snapshot;
    string db;
    string? sequence;
    string schema;
    string 'table;
    string? change_lsn;
    string? commit_lsn;
    int? event_serial_no;
};

public type Block2 record {
    string id;
    int total_order;
    int data_collection_order;
};

public type Envelope2 record {
    Value2? before;
    Value2? after;
    Source2 'source;
    string op;
    int? ts_ms;
    Block2? 'transaction;
    string? MessageSource;
};

public type UnionEnumRecord record {
    string|Numbers? field1;
};

public type UnionFixedRecord record {
    string|byte[]? field1;
};

public type UnionRec record {
    string|UnionEnumRecord? field1;
};

public type ReadOnlyRec readonly & record {
    string|UnionEnumRecord? & readonly field1;
};

type Record record {
    decimal? hrdCasinoGgrLt;
    string? hrdAccountCreationTimestamp;
    float? hrdSportsBetCountLifetime;
    float? hrdSportsFreeBetAmountLifetime;
    string? hriUnityId;
    string? hrdLastRealMoneySportsbookBetTs;
    string? hrdLoyaltyTier;
    string? rowInsertTimestampEst;
    float? ltvSports365Total;
    string? hrdFirstDepositTimestamp;
    boolean? hrdVipStatus;
    string? hrdLastRealMoneyCasinoWagerTs;
    float? hrdSportsCashBetAmountLifetime;
    string? hrdAccountStatus;
    string? hrdAccountId;
    float? ltvAllVerticals365Total;
    boolean? hrdOptInSms;
    float? ltvCasino365Total;
    string? hrdAccountSubStatus;
    float? hrdCasinoTotalWagerLifetime;
    float? hrdSportsGgrLt;
    string? currentGeoSegment;
    string? kycStatus;
    string? signupGeoSegment;
    float? hrdSportsBetAmountLifetime;
    boolean? hrdOptInEmail;
    boolean? hrdOptInPush;
};

type ReadOnlyUnionFixed UnionFixedRecord & readonly;
type ByteArray byte[];
type ReadOnlyIntArray int[] & readonly;
type ReadOnlyStringArray string[] & readonly;
type StringArray string[];
type String2DArray string[][];
type ReadOnlyColors Color & readonly;
type NullType ();
type ByteArrayMap map<byte[]>;
type MapOfByteArrayMap map<map<byte[]>>;
type ReadOnlyMapOfReadOnlyRecord map<Instructor & readonly> & readonly;
type ReadOnlyMapOfRecord map<Instructor> & readonly;
type RecordMap map<Instructor>;
type IntMap map<int>;
type EnumMap map<Numbers>;
type EnumArrayMap map<Numbers[]>;
type FloatMap map<float>;
type FloatArrayMap map<float[]>;
type StringMap map<string>;
type BooleanMap map<boolean>;
type ReadOnlyBooleanMap map<boolean> & readonly;
type MapOfIntMap map<map<int>>;
type ReadOnlyMapOfReadOnlyMap map<map<map<int>>> & readonly;
type MapOfMap map<map<map<int>>>;
type IntArrayMap map<int[]>;
type ReadOnlyIntArrayMap map<int[]> & readonly;
type StringArrayMap map<string[]>;
type ByteArrayArrayMap map<byte[][]>;
type BooleanArrayMap map<boolean[]>;
type MapOfRecordArray map<Instructor[]>;
type MapOfArrayOfRecordArray map<Instructor[][]>;
type StringArrayArrayMap map<string[][]>;
type MapOfRecordMap map<map<Lecturer>>;
type MapOfRecordArrayMap map<map<Lecturer[]>>;
type ReadOnlyMapOfRecordArray map<map<Lecturer[]>> & readonly;
type ArrayOfByteArray byte[][];
type ReadOnlyStudent2DArray Student[][] & readonly;
type Student2DArray Student[][];
type ReadOnlyStudentArray Student[] & readonly;
type StudentArray Student[];
type BooleanArray boolean[];
type IntArray int[];
type FloatArray float[];
type EnumArray Numbers[];
type Enum2DArray Numbers[][];
type ReadOnlyString2DArray string[][] & readonly;
type DataRecord record{};
