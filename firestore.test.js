const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_kinshasa";
const BOB_UID = "bob_lubumbashi";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read products", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("products").get());
});

test("Authenticated user: can read products catalog", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("products").get());
});

test("Authenticated user: can create product with valid data", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("products").doc("prod_1").set({
      title: "Robe Pagne Woodin",
      sellerId: ALICE_UID,
      priceUsd: 25.0,
      city: "Kinshasa",
      categoryId: "wax",
      imageUrl: "https://example.com/photo.jpg",
      createdAt: new Date(),
    })
  );
});

test("Spoofing: cannot create product with another user's sellerId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("products").doc("prod_2").set({
      title: "Robe Pagne Woodin",
      sellerId: BOB_UID, // Spoofed!
      priceUsd: 25.0,
      city: "Kinshasa",
      categoryId: "wax",
      imageUrl: "https://example.com/photo.jpg",
      createdAt: new Date(),
    })
  );
});

test("Orders: user can create and read their own order", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").doc("order_1").set({
      buyerId: ALICE_UID,
      productId: "prod_1",
      totalUsd: 28.0,
      deliveryAddress: "Gombe Kinshasa",
      phoneNumber: "+243820000000",
      paymentMethod: "MPESA",
      status: "PROCESSING",
      trackingCode: "SHP-123",
      createdAt: new Date(),
    })
  );

  // Alice can read her own order
  await assertSucceeds(aliceDb.collection("orders").doc("order_1").get());

  // Bob cannot read Alice's order
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("orders").doc("order_1").get());
});
