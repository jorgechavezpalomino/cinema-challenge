import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import React from "react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import App from "./App";
import { AppProvider } from "./state/AppContext";
import { BrowserRouter } from "react-router-dom";

const premieres = [
  { id: 1, title: "Mision Estelar", description: "Premiere de accion.", imageUrl: "https://image/1" }
];

const candystore = [
  { id: 1, name: "Combo Clasico", description: "Canchita y gaseosa", price: 29.9 }
];

function setupFetchMock() {
  global.fetch = vi.fn(async (url, options = {}) => {
    if (url.endsWith("/premieres")) {
      return { ok: true, json: async () => premieres };
    }
    if (url.endsWith("/candystore")) {
      return { ok: true, json: async () => candystore };
    }
    if (url.endsWith("/auth/guest")) {
      return {
        ok: true,
        json: async () => ({
          token: "guest-token",
          user: { email: "guest@cinema.local", name: "Invitado", guest: true }
        })
      };
    }
    if (url.endsWith("/checkout")) {
      return {
        ok: true,
        json: async () => ({ code: "0", message: "Compra registrada correctamente" })
      };
    }
    throw new Error(`Unexpected fetch call: ${url} ${options.method ?? "GET"}`);
  });
}

describe("Guest checkout flow", () => {
  beforeEach(() => {
    setupFetchMock();
    window.google = {
      accounts: {
        id: {
          initialize: vi.fn(),
          renderButton: vi.fn()
        }
      }
    };
    window.history.pushState({}, "", "/");
  });

  afterEach(() => {
    vi.restoreAllMocks();
    delete window.google;
    localStorage.clear();
  });

  it("completes the guest purchase flow", async () => {
    const user = userEvent.setup();

    render(
      <BrowserRouter>
        <AppProvider>
          <App />
        </AppProvider>
      </BrowserRouter>
    );

    await user.click(await screen.findByRole("button", { name: /mision estelar/i }));
    await user.click(screen.getByRole("button", { name: /invitado/i }));
    await user.click(await screen.findByRole("button", { name: /agregar/i }));
    await user.click(screen.getByRole("button", { name: /continuar/i }));

    await user.type(screen.getByLabelText(/numero de tarjeta/i), "4907840000000005");
    await user.type(screen.getByLabelText(/fecha de expiracion/i), "05/30");
    await user.type(screen.getByLabelText(/^cvv$/i), "777");
    await user.clear(screen.getByLabelText(/correo electronico/i));
    await user.type(screen.getByLabelText(/correo electronico/i), "guest@test.com");
    await user.clear(screen.getByLabelText(/^nombre$/i));
    await user.type(screen.getByLabelText(/^nombre$/i), "APPROVED USER");
    await user.type(screen.getByLabelText(/numero de documento/i), "12345678");

    await user.click(screen.getByRole("button", { name: /pagar s\/ 29\.90/i }));

    await waitFor(() => expect(screen.getByText(/compra correcta/i)).toBeInTheDocument());
  });
});
