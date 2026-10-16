import * as SecureStore from "expo-secure-store";
import { useEffect, useMemo, useState, type ReactNode } from "react";

import { AuthContext } from "@/contexts/AuthContext";
import { api, setSessionExpiredHandler } from "@/libs/api";
import { isTokenExpired } from "@/libs/jwt";
import { syncCarSession } from "../../modules/micar-car";

const TOKEN_KEY = "token";

type AuthProviderProps = {
  children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
  const [token, setToken_] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    SecureStore.getItemAsync(TOKEN_KEY).then((storedToken) => {
      const validToken =
        storedToken && !isTokenExpired(storedToken) ? storedToken : null;

      if (storedToken && !validToken) {
        SecureStore.deleteItemAsync(TOKEN_KEY);
      }

      setToken_(validToken);
      setIsLoading(false);
    });
  }, []);

  useEffect(() => {
    if (token) {
      api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    } else {
      delete api.defaults.headers.common["Authorization"];
    }
  }, [token]);

  useEffect(() => {
    if (!isLoading) {
      syncCarSession(token);
    }
  }, [token, isLoading]);

  function setToken(newToken: string | null) {
    setToken_(newToken);

    if (newToken) {
      SecureStore.setItemAsync(TOKEN_KEY, newToken);
    } else {
      SecureStore.deleteItemAsync(TOKEN_KEY);
    }
  }

  useEffect(() => {
    setSessionExpiredHandler(() => setToken(null));
    return () => setSessionExpiredHandler(null);
  }, []);

  const contextValue = useMemo(
    () => ({ token, isLoading, setToken }),
    [token, isLoading],
  );

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
}
