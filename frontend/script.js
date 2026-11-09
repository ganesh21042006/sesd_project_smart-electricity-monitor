const API = "http://localhost:8080/api/electricity";
let chart;

async function loadDashboard() {
  const response = await fetch(`${API}/dashboard`);
  if (!response.ok) throw new Error("Could not load dashboard");
  const d = await response.json();
  document.getElementById("units").textContent = Number(d.monthlyUnits).toFixed(1);
  document.getElementById("predicted").textContent = Number(d.predictedUnits).toFixed(1);
  document.getElementById("remaining").textContent = Number(d.remainingUnits).toFixed(1);
  document.getElementById("bill").textContent = `₹${Number(d.estimatedBill).toFixed(2)}`;
  document.getElementById("status").textContent = d.status;
  const pct = Math.min(100, Number(d.predictedUnits) / 200 * 100);
  document.getElementById("percent").textContent = `${pct.toFixed(0)}%`;
  const bar = document.getElementById("progressBar");
  bar.style.width = `${pct}%`;
  bar.style.background = pct >= 100 ? "#dc5656" : pct >= 90 ? "#e7a33e" : "#16a078";
  const list = document.getElementById("recommendations");
  list.replaceChildren();
  d.recommendations.forEach(t => { const li=document.createElement("li"); li.textContent=t; list.appendChild(li); });
}

async function loadHistory() {
  const response = await fetch(`${API}/history`);
  if (!response.ok) throw new Error("Could not load history");
  const rows = await response.json();
  const body = document.getElementById("history");
  body.replaceChildren();
  if (!rows.length) {
    const tr=document.createElement("tr"), td=document.createElement("td");
    td.colSpan=3; td.textContent="No readings yet"; tr.appendChild(td); body.appendChild(tr);
  }
  rows.forEach(r => {
    const tr=document.createElement("tr");
    [r.readingDate, Number(r.meterReading).toFixed(2), Number(r.unitsConsumed).toFixed(2)].forEach(v => {
      const td=document.createElement("td"); td.textContent=v; tr.appendChild(td);
    });
    body.appendChild(tr);
  });
  if (chart) chart.destroy();
  chart = new Chart(document.getElementById("usageChart"), {
    type:"bar",
    data:{labels:rows.map(r=>r.readingDate),datasets:[{label:"Units since previous reading",data:rows.map(r=>r.unitsConsumed),backgroundColor:"#15947f",borderRadius:5}]},
    options:{responsive:true,plugins:{legend:{display:true}},scales:{y:{beginAtZero:true}}}
  });
}

async function refreshAll() {
  try { await Promise.all([loadDashboard(), loadHistory()]); }
  catch(e) { document.getElementById("status").textContent="Backend not connected. Start Spring Boot and refresh."; }
}

document.getElementById("readingForm").addEventListener("submit", async e => {
  e.preventDefault();
  const message=document.getElementById("message");
  const payload={readingDate:document.getElementById("date").value,meterReading:Number(document.getElementById("reading").value)};
  try {
    const res=await fetch(`${API}/reading`,{method:"POST",headers:{"Content-Type":"application/json"},body:JSON.stringify(payload)});
    if(!res.ok){let msg=await res.text(); throw new Error(msg || "Unable to save reading");}
    message.textContent="Reading saved successfully.";
    e.target.reset();
    await refreshAll();
  } catch(err) { message.textContent=err.message; }
});

document.getElementById("date").value = new Date().toISOString().slice(0,10);
refreshAll();
