# Project Requirements & ASIA Submission

**Capstone and Advanced Systems Integration & Architecture (ASIA) Project Materials**

This directory contains all project requirements, capstone documentation, and ASIA semestral project materials.

## Contents

### ASIA Semestral Materials (IT 322)

Complete enterprise project submission with production-ready documentation:

#### **Material 1: Enterprise System Alignment**
📄 `ASIA_Material_1_Enterprise_Alignment.pdf` | `ASIA_Material_1_Enterprise_Alignment.md`

**Content:**
- Executive summary (production system, not prototype)
- Complete 3-tier architecture design
- Multi-branch deployment topology (Lipa & San Pablo)
- Technology stack mapping for each tier
- Integration & interoperability plan
- High availability and infrastructure strategy
- Gap analysis: Capstone vs. ASIA requirements
- Production readiness checklist

**Key Sections:**
- Tier 1: Presentation (Android app)
- Tier 2: Business Logic (Repositories, edge processing, cloud failover)
- Tier 3: Data (Firestore collections with security rules)
- Multi-branch deployment (99.9% SLA)
- Integration with 8+ services

**When to Use:** For understanding the overall system architecture and enterprise positioning.

---

#### **Material 2: Quality & Performance Analysis**
📄 `ASIA_Material_2_Quality_Performance.pdf` | `ASIA_Material_2_Quality_Performance.md`

**Content:**
- ISO 25010 quality model prioritization radar
- Security (100%) and Reliability (95%) at highest priority
- 10-layer data integrity architecture
- Conceptual data path through 3-tier system
- Performance scaling curves (green/yellow/red zones)
- Phase-based scaling strategy (5 → 100 concurrent users)
- Load testing assumptions and benchmarks

**Key Features:**
- Quality trade-offs explained
- Data integrity checks at each layer
- Performance metrics per phase
- Bottleneck identification
- Scaling recommendations

**When to Use:** For quality assurance, performance validation, and demonstrating enterprise-grade design.

---

#### **Material 3: Technical Integration Registry**
📄 `ASIA_Material_3_Integration_Registry.pdf` | `ASIA_Material_3_Integration_Registry.md`

**Content:**
- 10-point integration inventory
  - Cloud services (Firebase Auth, Firestore, FCM, Cloud Vision)
  - Hardware (camera, temp sensor, fan, LEDs)
  - ML/Vision (YOLOv8, OpenCV)
- Detailed integration specifications with endpoints and payloads
- Technical layer mapping (code structure proof)
- Firestore schema documentation
- Security rules enforcement
- Evidence collection procedures

**Mandatory Evidence:**
- Firebase Auth API test (Postman)
- Folder structure proof (IDE file tree)
- Security verification (passwords hashed, API keys in .env)
- Firestore security rules display

**When to Use:** For proving integration maturity and providing deployment guidance.

---

### Capstone Project Materials

#### **Capstone Manuscript**
📄 `capstone_manuscript.pdf`

**Contains:**
- Complete capstone project documentation (141 pages)
- Chapter 1: Project context and problem statement
- Chapter 2: Related systems and technical background
- Chapter 3: Design and methodology
- System architecture diagrams
- Hardware specification schematics
- Software and functional requirements
- YOLO detection model information
- Risk management and deployment strategy

**Reference:** This is the foundational document that the ASIA materials build upon.

---

### Original Submission Materials

📁 `capstone_materials/`

**Legacy Files:**
- Original ASIA submission PDFs (if any)
- Related project context documents
- Supporting materials from original submission

---

## How to Use These Materials

### For ASIA Semestral Presentation

**1. Before Defense (Week 2-3)**
- [ ] Read all three ASIA Materials in order
- [ ] Understand 3-tier architecture (Material 1)
- [ ] Review quality prioritization (Material 2)
- [ ] Prepare evidence screenshots (Material 3)
- [ ] Test integration points on Firestore

**2. Evidence Preparation (Week 3)**
- [ ] Screenshot Firebase Auth login test
- [ ] Capture folder structure from IDE
- [ ] Show Firestore security rules
- [ ] Prepare live demo of 3-tier flow
- [ ] Package all evidence with labels

**3. Defense Presentation (Week 4)**
- [ ] Present Materials 1-3 in order
- [ ] Show live system demonstration
- [ ] Reference cap ture evidence screenshots
- [ ] Be ready to answer architecture questions
- [ ] Explain design trade-offs (Material 2)

### For Understanding System Design

**Material 1** → System overview and architecture
↓
**Material 2** → Design quality and performance
↓
**Material 3** → Integration implementation details

### For Development/Deployment

- **Architecture decisions**: Section 2 of Material 1
- **Data flow**: Section 3 & Appendix of Material 1
- **Security requirements**: Material 2 + Material 3 (security checks)
- **Integration specs**: Material 3 (each integration section)
- **Deployment guide**: Adjacent `docs/deployment/` folder

---

## Key Takeaways

### Why These Materials Matter

**Material 1 (Alignment)** proves:
- ✓ System follows enterprise 3-tier pattern
- ✓ No direct database access from UI layer
- ✓ Multi-branch deployment architecture
- ✓ Cloud-ready infrastructure

**Material 2 (Quality)** proves:
- ✓ Security and reliability are prioritized
- ✓ Data integrity built into every layer
- ✓ Performance scales to 100+ users
- ✓ Professional engineering practices

**Material 3 (Integration)** proves:
- ✓ Real, working integrations (not theoretical)
- ✓ Code structure matches architecture
- ✓ Security implemented (hashed passwords, secured keys)
- ✓ System deployment-ready

---

## Required Reading Order

For new team members or reviewers:

1. **Start here:** `README.md` (main project)
2. **System overview:** Sections 1–2 of Material 1
3. **Architecture visual:** Figures in Material 1
4. **Quality justification:** Material 2 intro + ISO 25010 section
5. **Integration details:** Material 3 Part A (inventory table)
6. **Evidence procedure:** Material 3 Part C
7. **Deep dive:** Appendices in each material

---

## Presentation Tips

### 3-5 Minute Defense

**Opening (30 sec):**
> "Otto Scents is a production-grade, enterprise-class inventory system. It's not a prototype—it's designed for immediate deployment to the two profit-making branches. Today we're proving it meets IT 322 standards through strict 3-tier architecture, comprehensive quality engineering, and verified integrations."

**Body (4 min):**
1. **Show folder structure** - Prove 3-tier separation (Material 3 evidence)
2. **Walk through architecture** - Display Material 1 diagram
3. **Highlight quality** - Reference Material 2 ISO radar and data integrity
4. **Demonstrate integration** - Show one working API call (Postman screenshot)
5. **Discuss scaling** - Reference performance curve (Material 2)

**Closing (30 sec):**
> "This system is ready for production deployment. All three materials demonstrate compliance with enterprise standards and IT 322 ASIA requirements."

---

## FAQ

**Q: Can I submit ASIA Materials without implementation?**  
A: No. Materials must reference actual code and live system. Proof screenshots are mandatory.

**Q: What if an integration isn't fully live?**  
A: Use mockups as specified in Material 3. Clearly label as "mocked" vs. "live" in evidence.

**Q: How accurate must performance metrics be?**  
A: Use actual measurements (load test your system). Estimates must be clearly labeled as such.

**Q: Can I modify these materials?**  
A: Yes. Customize with your specific system metrics, but keep structure and evidence procedures.

**Q: What if Material 3 procedures don't match my system?**  
A: Adapt procedures to your integrations. Key is: prove the integration works with evidence.

---

## Additional Resources

- **Complete system**: Root README.md
- **Architecture deep dive**: `docs/architecture/`
- **Deployment guide**: `docs/deployment/`
- **API reference**: `docs/api/firestore_collections.md`
- **Capstone original**: `capstone_manuscript.pdf`

---

## Defense Checklist

- [ ] Read all three ASIA Materials
- [ ] Prepare evidence screenshots per Material 3
- [ ] Test live system (3-tier flow end-to-end)
- [ ] Practice 5-minute presentation
- [ ] Know your architecture trade-offs (Material 2)
- [ ] Be ready to discuss scaling strategy
- [ ] Explain why Otto Scents is "production," not "prototype"
- [ ] Bring backup devices with full environment if possible
- [ ] Have printouts of all materials

---

**Last Updated:** May 2026  
**Project Status:** IT 322 ASIA Ready  
**Submission Status:** Complete & Defense-Ready

