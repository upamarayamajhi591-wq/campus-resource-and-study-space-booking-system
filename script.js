const resources=[
{name:"Study Room A",category:"Study Room",location:"Building A",status:"Available",image:"https://picsum.photos/400/250?1"},
{name:"Computer Lab 2",category:"Computer Lab",location:"Building B",status:"Booked",image:"https://picsum.photos/400/250?2"},
{name:"Conference Hall",category:"Conference Room",location:"Building C",status:"Available",image:"https://picsum.photos/400/250?3"},
{name:"Study Room B",category:"Study Room",location:"Building D",status:"Available",image:"https://picsum.photos/400/250?4"}];
const c=document.getElementById("resourceContainer");
function display(list){c.innerHTML="";list.forEach(r=>c.innerHTML+=`<div class="card"><img src="${r.image}"><div class="card-content"><h3>${r.name}</h3><p><b>Category:</b> ${r.category}</p><p><b>Location:</b> ${r.location}</p><span class="status ${r.status==="Available"?"available":"booked"}">${r.status}</span><button>Book Now</button></div></div>`)}
display(resources);
search.onkeyup=e=>display(resources.filter(r=>r.name.toLowerCase().includes(search.value.toLowerCase())||r.location.toLowerCase().includes(search.value.toLowerCase())));
categoryFilter.onchange=e=>display(categoryFilter.value==="all"?resources:resources.filter(r=>r.category===categoryFilter.value));
