const { useEffect, useMemo, useState } = React;

const API_URL = "http://localhost:8080/auditoria-mensageria";

function formatDate(value) {
  if (!value) {
    return "-";
  }

  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "medium",
  }).format(new Date(value));
}

function tryFormatJson(value) {
  try {
    return JSON.stringify(JSON.parse(value), null, 2);
  } catch (error) {
    return value || "";
  }
}

function App() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadLogs() {
    setLoading(true);
    setError("");

    try {
      const response = await fetch(API_URL);

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      setLogs(await response.json());
    } catch (requestError) {
      setError("Nao foi possivel carregar a auditoria. Confira se a API esta rodando em localhost:8080.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadLogs();
    const intervalId = setInterval(loadLogs, 5000);
    return () => clearInterval(intervalId);
  }, []);

  const totals = useMemo(() => {
    return logs.reduce(
      (acc, log) => {
        acc.total += 1;
        if (log.direction === "PUBLISHED") {
          acc.published += 1;
        }
        if (log.direction === "CONSUMED") {
          acc.consumed += 1;
        }
        return acc;
      },
      { total: 0, published: 0, consumed: 0 }
    );
  }, [logs]);

  return (
    React.createElement("main", { className: "app-shell" },
      React.createElement("header", { className: "page-header" },
        React.createElement("div", null,
          React.createElement("p", { className: "eyebrow" }, "DevFacil"),
          React.createElement("h1", null, "Auditoria de Mensageria")
        ),
        React.createElement("button", { type: "button", onClick: loadLogs, disabled: loading },
          loading ? "Atualizando..." : "Atualizar"
        )
      ),

      React.createElement("section", { className: "metrics", "aria-label": "Resumo da auditoria" },
        React.createElement("article", null,
          React.createElement("span", null, "Total"),
          React.createElement("strong", null, totals.total)
        ),
        React.createElement("article", null,
          React.createElement("span", null, "Publicadas"),
          React.createElement("strong", null, totals.published)
        ),
        React.createElement("article", null,
          React.createElement("span", null, "Consumidas"),
          React.createElement("strong", null, totals.consumed)
        )
      ),

      error && React.createElement("p", { className: "error" }, error),

      React.createElement("section", { className: "table-wrap" },
        React.createElement("table", null,
          React.createElement("thead", null,
            React.createElement("tr", null,
              React.createElement("th", null, "Data"),
              React.createElement("th", null, "Direcao"),
              React.createElement("th", null, "Evento"),
              React.createElement("th", null, "Exchange"),
              React.createElement("th", null, "Routing key"),
              React.createElement("th", null, "Fila"),
              React.createElement("th", null, "Payload")
            )
          ),
          React.createElement("tbody", null,
            logs.length === 0 && !loading
              ? React.createElement("tr", null,
                  React.createElement("td", { colSpan: "7", className: "empty" }, "Nenhum log de mensageria registrado.")
                )
              : logs.map((log) =>
                  React.createElement("tr", { key: log.id },
                    React.createElement("td", null, formatDate(log.createdAt)),
                    React.createElement("td", null,
                      React.createElement("span", { className: `badge ${log.direction.toLowerCase()}` }, log.direction)
                    ),
                    React.createElement("td", null, log.eventName),
                    React.createElement("td", null, log.exchangeName),
                    React.createElement("td", null, log.routingKey),
                    React.createElement("td", null, log.queueName || "-"),
                    React.createElement("td", null,
                      React.createElement("pre", null, tryFormatJson(log.payload))
                    )
                  )
                )
          )
        )
      )
    )
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(React.createElement(App));
