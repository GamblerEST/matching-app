export async function GetConnections() {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/connections", {
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
  return response.json();
}

export async function SendConnectionRequest(userId: number) {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/connections/request/" + userId, {
    method: "POST",
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
  return response.status;
}

export async function GetPendingConnectionRequest() {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/connections/requests", {
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
  return response.json();
}

export async function AcceptConnectionRequest(connectionId: number | undefined) {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/connections/" + connectionId + "/accept", {
    method: "PUT",
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
  return response.status;
}

export async function RejectConnectionRequest(connectionId: number | undefined) {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/connections/" + connectionId + "/reject", {
    method: "PUT",
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
  return response.status;
}

export async function DisconnectConnection(userId: number) {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/connections/" + userId, {
    method: "DELETE",
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
  return response.status;
}