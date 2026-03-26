import { ProfileInfo } from "../interfaces/ProfileInfo";

export async function GetMyProfile() {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/users/me/profile", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  if (!response.ok) throw new Error("Login failed");
  const responseData = await response.json();
  return responseData;
}

export async function GetUserProfile(id: number) {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/users/" + id + "/profile", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  if (!response.ok) {
    const errorBody = await response.json();
    // eslint-disable-next-line no-throw-literal
    throw {
      status: response.status,
      message: errorBody.error,
    };
  }
  const responseData = await response.json();
  return responseData;
}

export async function UpdateProfile(data: ProfileInfo): Promise<number> {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/profile/" + data.userId, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(data),
  });
  if (!response.ok) {
    const errorBody = await response.json();
    // eslint-disable-next-line no-throw-literal
    throw {
      status: response.status,
      message: errorBody.error,
    };
  }
  return response.status;
}

export async function UpdateMyProfilePicture(file: File) {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }

  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch("/me/profile/picture", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
    body: formData,
  });

  if (!response.ok) {
    const errorBody = await response.json();
    // eslint-disable-next-line no-throw-literal
    throw {
      status: response.status,
      message: errorBody.error,
    };
  }
  const responseData = await response.json();
  return responseData;
}

export async function DeleteMyProfilePicture(): Promise<number> {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/me/profile/picture", {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  if (!response.ok) {
    const errorBody = await response.json();
    // eslint-disable-next-line no-throw-literal
    throw {
      status: response.status,
      message: errorBody.error,
    };
  }
  return response.status;
}
