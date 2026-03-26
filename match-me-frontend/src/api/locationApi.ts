import { ProfileLocation } from "../interfaces/ProfileLocation";

export async function GetMyLocation() {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/locations", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  if (!response.ok) throw new Error("Getting Location failed");
  const responseData = await response.json();
  return responseData;
}

export async function UpdateLocation(data: ProfileLocation): Promise<number> {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/locations", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    throw new Error("Creating Locations failed");
  }
  return response.status;
}
